plugins {
    id("java")
    application
}

group = "org.kanelucky"
version = "0.1.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

application {
    mainClass.set("org.kanelucky.mobmind.example.ExampleServer")
}

tasks.run.get().workingDir = file("run")

dependencies {
    implementation(libs.minestom)
    implementation(project(":api"))
    implementation(project(":core"))
    implementation(project(":vanilla"))
}


tasks.test {
    useJUnitPlatform()
}