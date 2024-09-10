package trplugins.menu.api.receptacle.vanilla.window

import net.minecraft.core.NonNullList
import net.minecraft.network.PacketDataSerializer
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot
import net.minecraft.network.protocol.game.PacketPlayOutWindowData
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems
import net.minecraft.world.inventory.Containers
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_20_R4.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.nms.dataSerializerBuilder
import taboolib.module.nms.sendPacket
import trplugins.menu.api.receptacle.vanilla.window.StaticInventory.inventoryView
import trplugins.menu.api.receptacle.vanilla.window.StaticInventory.staticInventory

/**
 * TrMenu
 * trplugins.menu.api.receptacle.vanilla.window.NMSImpl2
 *
 * @author mical
 * @date 2024/9/10 16:52
 */
class NMSImpl2 : NMS() {

    private val emptyItemStack: net.minecraft.world.item.ItemStack? = CraftItemStack.asNMSCopy((ItemStack(Material.AIR)))

    private val windowIds = HashMap<String, Int>()

    private val Player.windowId get() = windowIds[this.name] ?: 119

    override fun windowId(player: Player, create: Boolean): Int {
        if (createWindowId() && create) {
            val id = player.getProperty<Int>("entity/containerCounter")!! + 1
            player.setProperty("entity/containerCounter", id)
            windowIds[player.name] = id
        }
        return player.windowId
    }

    override fun sendWindowsClose(player: Player, windowId: Int) {
        if (player.useStaticInventory()) {
            StaticInventory.close(player)
            return
        }
        player.sendPacket(PacketPlayOutCloseWindow.STREAM_CODEC.decode(dataSerializerBuilder {
            writeInt(windowId)
        }.build() as PacketDataSerializer))
    }

    override fun sendWindowsItems(player: Player, windowId: Int, items: Array<ItemStack?>) {
        if (player.useStaticInventory()) {
            val inventory = player.staticInventory!!
            items.forEachIndexed { index, item ->
                if (index >= inventory.size) {
                    return
                }
                inventory.setItem(index, item)
            }
            return
        }
        player.sendPacket(PacketPlayOutWindowItems(
            windowId, -1,
            NonNullList.create<net.minecraft.world.item.ItemStack>().also {
                it.addAll(items.map(CraftItemStack::asNMSCopy))
            },
            emptyItemStack
        ))
    }

    override fun sendWindowsOpen(player: Player, windowId: Int, type: WindowLayout, title: String) {
        if (player.useStaticInventory()) {
            StaticInventory.open(player, type, title)
            return
        }
        val windowType = Containers::class.java.getProperty<Containers<*>>(type.vanillaId, true)
        val component = if (title.startsWith('{') && title.startsWith('}')) {
            CraftChatMessage.fromJSON(title)
        } else {
            CraftChatMessage.fromString(title)[0]
        }
        player.sendPacket(PacketPlayOutOpenWindow(windowId, windowType, component))
    }

    override fun sendWindowsSetSlot(player: Player, windowId: Int, slot: Int, itemStack: ItemStack?, stateId: Int) {
        if (player.useStaticInventory()) {
            if (windowId == -1 && slot == -1) {
                player.itemOnCursor.type = Material.AIR
            } else {
                val inventory = player.staticInventory!!
                if (slot >= 0 && slot < inventory.size) {
                    inventory.setItem(slot, itemStack)
                }
            }
            return
        }
        player.sendPacket(PacketPlayOutSetSlot(windowId, -1, slot, CraftItemStack.asNMSCopy(itemStack)))
    }

    override fun sendWindowsUpdateData(player: Player, windowId: Int, id: Int, value: Int) {
        if (player.useStaticInventory()) {
            val inventory = player.staticInventory!!
            val view = player.inventoryView!!
            val property = getInventoryProperty(inventory.type, id) ?: return
            view.setProperty(property, value)
            return
        }
        player.sendPacket(PacketPlayOutWindowData.STREAM_CODEC.decode(dataSerializerBuilder {
            writeInt(windowId)
            writeInt(id)
            writeInt(value)
        }.build() as PacketDataSerializer))
    }

    private fun getInventoryProperty(type: InventoryType, id: Int): InventoryView.Property? {
        return InventoryView.Property.values().find { (it.type == type || (it.type == InventoryType.FURNACE && type == InventoryType.BLAST_FURNACE)) && it.id == id }
    }
}