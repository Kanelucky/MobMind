plugins {
    id("java")
}

group = "org.kanelucky"
version = "0.1.3"

dependencies {
    compileOnly(libs.minestom)
    implementation(project(":api"))
    implementation(project(":core"))
}

tasks.test {
    useJUnitPlatform()
}