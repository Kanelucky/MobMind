rootProject.name = "MobMind"
include("core")
include("api")
include("example")
include("vanilla")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}