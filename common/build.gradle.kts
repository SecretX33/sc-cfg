dependencies {
    api(project(":sccfg-api"))
    implementation(project(":sccfg-base-serializer"))
    implementation("org.reflections:reflections:0.10.2")
    compileOnly("com.google.guava:guava:31.0.1-jre")
    testImplementation("com.google.code.gson:gson:2.8.8")
    testImplementation("com.google.guava:guava:31.0.1-jre")
    compileOnly(project(":sccfg-hocon"))
    compileOnly(project(":sccfg-json"))
    compileOnly(project(":sccfg-yaml"))
    testImplementation(project(":sccfg-hocon"))
    testImplementation(project(":sccfg-json"))
    testImplementation(project(":sccfg-yaml"))
}
