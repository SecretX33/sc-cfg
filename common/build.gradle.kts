dependencies {
    compileOnly(project(":${rootProject.name}-api"))
    implementation("org.reflections:reflections:0.10.1")
    compileOnly("com.google.code.gson:gson:2.8.8")
    compileOnly("com.google.guava:guava:31.0.1-jre")
}
