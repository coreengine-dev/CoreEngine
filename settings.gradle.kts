/*
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform


pluginManagement {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("org.jetbrains.intellij.platform.settings") version "2.7.2"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        intellijPlatform { defaultRepositories() }
        google()
    }
}
rootProject.name = "CoreEngine"
include(
    ":coreengine-api",
    ":coreengine-runtime",
    ":coreengine-render-canvas",
    ":coreengine-render-gl",
    ":coreengine-android-host",
    ":coreengine-integration",
    ":samples:demo-app",
    ":ide:plugin"
)
*/


import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

pluginManagement {
    repositories { gradlePluginPortal(); mavenCentral();  google() }
}
plugins {
    id("org.jetbrains.intellij.platform.settings") version "2.7.2"
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        intellijPlatform { defaultRepositories() } // ← imprescindible
        google()
    }
}
rootProject.name = "CoreEngine"
include(
    ":coreengine-api",
    ":coreengine-runtime",
    ":coreengine-render-canvas",
    ":coreengine-render-gl",
    ":coreengine-android-host",
    ":coreengine-integration",
    ":samples:demo-app",
    ":ide:plugin"
)

