package trplugins.menu.api.receptacle.vanilla.window

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.InventoryView

object StaticInventory {

    private val inventories = HashMap<String, Holder>()

    val Player.staticInventory get() = inventories[this.name]?.inventory
    val Player.inventoryView get() = inventories[this.name]?.view

    fun open(player: Player, layout: WindowLayout, title: String) {
        val holder = Holder(layout, title)
        holder.open(player)
        inventories[player.name] = holder
    }

    fun close(player: Player) {
        player.closeInventory()
        inventories.remove(player.name)?.clear()
    }

    class Holder(layout: WindowLayout, title: String) : InventoryHolder {

        private val inventory: Inventory = when (val type = layout.toBukkitType()) {
            InventoryType.CHEST -> Bukkit.createInventory(this, layout.slotRange.last + 1, title)
            else -> Bukkit.createInventory(this, type, title)
        }
        var view: InventoryView? = null
            private set

        override fun getInventory(): Inventory {
            return inventory
        }

        fun open(player: Player) {
            view = player.openInventory(inventory)
        }

        fun clear() {
            inventory.clear()
            view = null
        }
    }
}