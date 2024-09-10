package trplugins.menu.api.receptacle

import org.bukkit.entity.Player
import taboolib.common.event.CancelableInternalEvent
import taboolib.common.event.InternalEvent

/**
 * @author Arasple
 * @date 2020/12/5 21:42
 */
class ReceptacleCloseEvent<Element>(val player: Player, val receptacle: Receptacle<Element>) : CancelableInternalEvent() {

    val allowCancelled: Boolean
        get() = false
}