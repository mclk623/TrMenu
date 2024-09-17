package trplugins.menu.util.bukkit

import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.common5.util.decodeBase64
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.util.modifyMeta
import trplugins.menu.module.internal.hook.HookPlugin
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * @author Arasple, Mical
 * @date 2021/1/27 14:05
 */
object Heads {

    private val DEFAULT_HEAD = XMaterial.PLAYER_HEAD.parseItem()!!
    private val CACHED_SKULLS = mutableMapOf<String, ItemStack>()
    private val VALUE = if (MinecraftVersion.major >= 1.20) "value" else "getValue"
    private val NAME = if (MinecraftVersion.major >= 1.20) "name" else "getName"

    private val JSON_PARSER = JsonParser()

    fun cacheSize(): Int {
        return CACHED_SKULLS.size
    }

    fun getHead(id: String): ItemStack {
        return if (id.length > 20) getCustomHead(id) else getPlayerHead(id)
    }

    private fun getCustomHead(id: String): ItemStack = CACHED_SKULLS.computeIfAbsent(id) {
        DEFAULT_HEAD.clone().modifyMeta<SkullMeta> {
            if (id.length <= 20) {
                owningPlayer = Bukkit.getOfflinePlayer(id)
                return@modifyMeta
            }
            // Spigot 1.18.1 发布之后添加的, 准确来说从 1.18.2 开始
            if (MinecraftVersion.versionId >= 11802) {
                val profile = Bukkit.createPlayerProfile(UUID(0, 0), "TabooLib")
                val textures = profile.textures
                // NOTICE 下面这行代码我不太清楚如何工作的, 但是工作正常, 来自 TrMenu
                val texture = if (id.length in 60..100) encodeTexture(id) else id
                val url = URL(getTextureURLFromBase64(texture))
                try {
                    textures.skin = url
                } catch (e: MalformedURLException) {
                    throw IllegalStateException("Invalid skull base64 content", e)
                }
                ownerProfile = profile
            } else {
                val profile = GameProfile(UUID(0, 0), "TabooLib")
                val texture = if (id.length in 60..100) encodeTexture(id) else id
                profile.properties.put("textures", Property("textures", texture, "TrMenu_TexturedSkull"))

                setProperty("profile", profile)
            }
        }
    }.clone()

    private fun getPlayerHead(name: String): ItemStack {
        if (HookPlugin.getSkinsRestorer().isHooked) {
            val texture: String? = HookPlugin.getSkinsRestorer().getPlayerSkinTexture(name)
            return texture?.let { getCustomHead(it) } ?: DEFAULT_HEAD
        }
        return getCustomHead(name)
    }

    fun seekTexture(itemStack: ItemStack): String? {
        val meta = itemStack.itemMeta ?: return null

        if (meta is SkullMeta) {
            meta.owningPlayer?.name?.let { return it }
        }

        meta.getProperty<GameProfile>("profile")?.properties?.values()?.forEach {
            if (it.getProperty<String>(NAME) == "textures") return it.getProperty<String>(VALUE)
        }
        return null
    }

    @Suppress("HttpUrlsUsage")
    private fun encodeTexture(input: String): String {
        return with(Base64.getEncoder()) {
            encodeToString("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/$input\"}}}".toByteArray())
        }
    }

    private fun getTextureURLFromBase64(headBase64: String): String {
        return JSON_PARSER
            .parse(String(headBase64.decodeBase64()))
            .asJsonObject
            .getAsJsonObject("textures")
            .getAsJsonObject("SKIN")
            .get("url")
            .asString
    }
}