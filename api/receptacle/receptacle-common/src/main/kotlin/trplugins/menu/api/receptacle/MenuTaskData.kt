package trplugins.menu.api.receptacle

data class MenuTaskData(
    val id: String,
    val period: Long,
    val actions: List<MenuTaskSubData>
)

data class MenuTaskSubData(
    val condition: String,
    val actions: List<String>
)
