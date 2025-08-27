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
    }
}

rootProject.name = "CoreEngine"
include(":engine")
include(":samples:demo-app")
include(":coreengine")
include(":coreengine-android-host")
include(":coreengine-integration")

include(":coreengine-render-canvas")
include(":coreengine-render-gl")
include(":coreengine-api")
include(":coreengine-runtime")
