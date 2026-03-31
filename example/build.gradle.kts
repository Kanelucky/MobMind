plugins {
    id("java")
    application
}

group = "org.kanelucky"
version = "0.1.0"

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

application {
    mainClass.set("org.kanelucky.mobmind.example.ExampleServer")
}

tasks.run.get().workingDir = file("run")

dependencies {
    implementation("net.minestom:minestom:2026.03.03-1.21.11")
    implementation(project(":api"))
    implementation(project(":core"))
    implementation(project(":vanilla"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


tasks.test {
    useJUnitPlatform()
}