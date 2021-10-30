rootProject.name = "sc-cfg"

setOf("api", "bukkit", "bukkit-kotlin", "common", "hocon", "json", "yaml").forEach {
    include(it)
}
