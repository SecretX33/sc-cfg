rootProject.name = "sc-cfg"

setOf("api", "bukkit", "bukkit-kotlin", "common", "hocon", "yaml").forEach {
    include(it)
}
