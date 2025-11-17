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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven( "https://jitpack.io")
    }
}

rootProject.name = "AquariumControl"
include(":app")
include(":balling")
include(":common")
include(":controller")
include(":feed")
include(":heating")
include(":info")
include(":overview")
include(":refill")
include(":schedule")
include(":timedata")
include(":ventilation")
