plugins {
    kotlin("jvm") version "2.3.0"
    id("java-library")
    id("maven-publish")
}

group = "org.kanelucky"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.minestom:minestom:2026.03.03-1.21.11")
    implementation("it.unimi.dsi:fastutil:8.5.15")
    implementation(project(":api"))
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "core"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}