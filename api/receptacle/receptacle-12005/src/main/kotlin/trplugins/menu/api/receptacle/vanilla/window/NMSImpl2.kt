package trplugins.menu.api.receptacle.vanilla.window

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * TrMenu
 * trplugins.menu.api.receptacle.vanilla.window.NMSImpl2
 *
 * @author mical
 * @date 2024/9/10 16:52
 */
class NMSImpl2 : NMS() {

    override fun windowId(player: Player, create: Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun sendWindowsClose(player: Player, windowId: Int) {
        TODO("Not yet implemented")
    }

    override fun sendWindowsItems(player: Player, windowId: Int, items: Array<ItemStack?>) {
        TODO("Not yet implemented")
    }

    override fun sendWindowsOpen(player: Player, windowId: Int, type: WindowLayout, title: String) {
        TODO("Not yet implemented")
    }

    override fun sendWindowsSetSlot(player: Player, windowId: Int, slot: Int, itemStack: ItemStack?, stateId: Int) {
        TODO("Not yet implemented")
    }

    override fun sendWindowsUpdateData(player: Player, windowId: Int, id: Int, value: Int) {
        TODO("Not yet implemented")
    }
}