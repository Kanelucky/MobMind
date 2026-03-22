plugins {
    kotlin("jvm") version "2.3.0"
}

group = "org.kanelucky"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}