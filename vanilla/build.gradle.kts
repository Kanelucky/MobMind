plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "org.kanelucky"
version = "0.2.0"

dependencies {
    compileOnly(libs.minestom)
    implementation(project(":api"))
    implementation(project(":core"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "vanilla"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}