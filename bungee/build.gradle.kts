repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    api(project(":api"))
    implementation(project(":common"))
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
}
