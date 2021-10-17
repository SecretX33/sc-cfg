repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(project(":api"))
    implementation(project(":common"))
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    implementation("com.github.cryptomorin:XSeries:8.4.0")
}
