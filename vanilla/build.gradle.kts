plugins {
    id("java")
}

group = "org.kanelucky"
version = "0.1.4"

dependencies {
    compileOnly(libs.minestom)
    implementation(project(":api"))
    implementation(project(":core"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.test {
    useJUnitPlatform()
}