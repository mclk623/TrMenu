package trplugins.menu.api.receptacle.hook

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import trplugins.menu.api.receptacle.provider.PlatformProvider

/**
 * TrMenu
 * trplugins.menu.api.receptacle.hook.HookFloodgate
 *
 * @author Score2
 * @since 2022/02/19 22:58
 */
class HookFloodgate private constructor() {

    fun isBedrockPlayer(player: Player): Boolean {
        return PlatformProvider.isBedrockPlayer(player)
    }

    companion object {

        private var hook: HookFloodgate? = null

        fun isBedrockPlayer(player: Player): Boolean {
            if (hook == null) {
                Bukkit.getPluginManager().getPlugin("floodgate") ?: return false
                hook = HookFloodgate()
            }
            return hook?.isBedrockPlayer(player) ?: false
        }
    }

}