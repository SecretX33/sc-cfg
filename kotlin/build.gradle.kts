repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(kotlin("stdlib"))
}
