rootProject.name = "TrMenu"

include(
    ":common",
    ":plugin",

    ":api:receptacle:receptacle-common",
    ":api:receptacle:receptacle-legacy",
    ":api:receptacle:receptacle-12005",
    ":api:receptacle:receptacle-12100",
    ":api:action"
)
include("api:receptacle:receptacle-common")
findProject(":api:receptacle:receptacle-common")?.name = "receptacle-common"
