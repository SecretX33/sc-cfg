import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
    compileOnly(project(":common"))
    compileOnly(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
