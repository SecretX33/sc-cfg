repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly(project(":sccfg-common"))
    compileOnly(kotlin("stdlib"))
}
