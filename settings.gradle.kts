rootProject.name = "sc-cfg"

include("api")
include("bukkit")
include("common")

listOf("api", "bukkit", "common").forEach {
    include(it)
    findProject(":$it")?.name = "${rootProject.name}-$it"
}
