dependencies {
    api(project(":api"))
    api("org.spongepowered:configurate-core:4.1.2")
    implementation("org.reflections:reflections:0.10.2")
    compileOnly("com.google.code.gson:gson:2.8.8")
    compileOnly("com.google.guava:guava:31.0.1-jre")
    testImplementation("com.google.code.gson:gson:2.8.8")
    testImplementation("com.google.guava:guava:31.0.1-jre")
    testImplementation(project(":hocon"))
    testImplementation(project(":json"))
    testImplementation(project(":yaml"))
}
