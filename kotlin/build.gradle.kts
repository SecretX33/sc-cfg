repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(kotlin("stdlib"))
    implementation("org.spongepowered:configurate-yaml:4.1.2")
}
