import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":bukkit"))
    compileOnly(project(":common"))
    compileOnly(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
