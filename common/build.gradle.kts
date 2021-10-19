dependencies {
    compileOnly(project(":api"))
    implementation("org.reflections:reflections:0.10.1")
    compileOnly("com.google.code.gson:gson:2.8.8")
    compileOnly("com.google.guava:guava:31.0.1-jre")
    implementation("org.spongepowered:configurate-core:4.1.2")
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("org.spongepowered:configurate-hocon:4.1.2")

    testImplementation(project(":api"))
    testImplementation("com.google.code.gson:gson:2.8.8")
    testImplementation("com.google.guava:guava:31.0.1-jre")
    testImplementation("org.spongepowered:configurate-yaml:4.1.2")
    testImplementation("org.spongepowered:configurate-hocon:4.1.2")
}
