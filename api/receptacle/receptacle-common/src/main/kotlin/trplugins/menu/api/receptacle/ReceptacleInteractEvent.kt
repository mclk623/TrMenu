package trplugins.menu.api.receptacle

import org.bukkit.entity.Player
import taboolib.common.event.CancelableInternalEvent

/**
 * @author Arasple
 * @date 2020/12/5 21:42
 */
class ReceptacleInteractEvent<Element>(val player: Player, val receptacle: Receptacle<Element>, val receptacleClickType: ReceptacleClickType, val slot: Int) : CancelableInternalEvent() {

    var element: Element?
        set(value) = receptacle.setElement(value, slot)
        get() = receptacle.getElement(slot)

    fun refresh() {
        if (receptacleClickType.isItemMoveable()) {
            receptacle.refresh()
        } else {
            receptacle.refresh(slot)
        }
    }
}