import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.hierynomus.license") version "0.16.1"
    kotlin("jvm") version "1.6.20"
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
    apply(plugin = "kotlin")

    dependencies {
        compileOnly("org.jetbrains:annotations:23.0.0")
        implementation("org.checkerframework:checker-qual:3.21.4")
        testImplementation(kotlin("stdlib-jdk8"))
        testImplementation(kotlin("test-junit5"))
        testImplementation(platform("org.junit:junit-bom:5.8.2"))
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter-params")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testImplementation("org.mockito:mockito-inline:4.4.0")
        testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    }

    tasks.test {
        useJUnitPlatform()
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                pom {
                    name.set(rootProject.name)
                    description.set("Library that dynamically generate config files from an instance of a class, greatly reducing development time.")
                    url.set("https://github.com/SecretX33/sc-cfg/")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("secretx33")
                            name.set("SecretX")
                            email.set("notyetmidnight@gmail.com")
                        }
                    }
                    scm {
                        url.set("https://github.com/SecretX33/sc-cfg/")
                    }
                }
            }
        }
    }

    tasks.javadoc {
        val options = options as StandardJavadocDocletOptions
        if (JavaVersion.current().isJava9Compatible) {
            options.addBooleanOption("html5", true)
        }
        options.addStringOption("Xdoclint:none", "-quiet")
    }

    license {
        header = rootProject.file("HEADER")
        strictCheck = true
        ext {
            set("yearRange", "2021-2022")
            set("name", "SecretX")
            set("email", "notyetmidnight@gmail.com")
        }
        mapping(mapOf(
            "java" to "SLASHSTAR_STYLE",
            "kt" to "SLASHSTAR_STYLE"
        ))
        exclude("**/*FileWatcher.java")
    }
}
