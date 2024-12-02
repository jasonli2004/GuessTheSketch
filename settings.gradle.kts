pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Ensures project repos are not allowed
    repositories {
        google()  // Includes Google's Maven repository
        mavenCentral() // Includes Maven Central repository
    }
}

rootProject.name = "GuessTheSketch"
include(":app")
