plugins {
    id("java")
}

group = "org.kanelucky"
version = "0.1.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom:2026.03.03-1.21.11")
    implementation(project(":api"))
    implementation(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}