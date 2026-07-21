plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "org.kanelucky"
version = "0.2.0"

dependencies {
    compileOnly(libs.minestom)
    implementation(libs.fastutil)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "api"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}