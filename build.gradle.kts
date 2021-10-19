plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.hierynomus.license") version "0.16.1"
    kotlin("jvm") version "1.5.31"
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
        testImplementation(kotlin("stdlib-jdk8"))
        testImplementation(kotlin("test-junit5"))
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
        testImplementation("org.mockito:mockito-inline:4.0.0")
        testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
        compileOnly("org.jetbrains:annotations:22.0.0")
        compileOnly("org.checkerframework:checker-qual:3.18.1")
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

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }

    tasks.javadoc {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    license {
        header = rootProject.file("HEADER")
        strictCheck = true
        ext {
            set("year", 2021)
            set("name", "SecretX")
            set("email", "notyetmidnight@gmail.com")
        }
        mapping(mapOf(
            "java" to "SLASHSTAR_STYLE",
            "kotlin" to "SLASHSTAR_STYLE"
        ))
        exclude("**/*FileWatcher.java")
    }

    /*create<MavenPublication>("mavenJava") {
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
                connection.set("scm:git:git://example.com/my-library.git")
                developerConnection.set("scm:git:ssh://example.com/my-library.git")
                url.set("https://github.com/SecretX33/sc-cfg/")
            }
        }
    }*/
}
