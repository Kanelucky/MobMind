plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
}

group = "org.kanelucky"
version = "0.1.2"

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}