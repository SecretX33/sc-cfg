rootProject.name = "sc-cfg"

setOf("api", "bukkit", "bukkit-kotlin", "common").forEach {
    include(it)
}
