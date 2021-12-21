repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    api(project(":sccfg-api"))
    implementation(project(":sccfg-common"))
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    implementation("com.github.cryptomorin:XSeries:[8.5.0,)")
    testImplementation("com.github.seeseemelk:MockBukkit:v1.16-SNAPSHOT")
}
