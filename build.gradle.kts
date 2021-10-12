plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.hierynomus.license") version "0.16.1"
}

allprojects {
    group = "com.github.secretx33"
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "license")

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
        compileOnly("org.jetbrains:annotations:22.0.0")
        compileOnly("org.checkerframework:checker-qual:3.18.1")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
    }
}





