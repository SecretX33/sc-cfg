rootProject.name = "sc-cfg"

listOf("api", "base-serializer", "bukkit", "bungee", "common", "hocon", "json", "kotlin", "yaml")
    .forEach {
        include(it)
        findProject(":$it")?.name = "sccfg-$it"
    }
