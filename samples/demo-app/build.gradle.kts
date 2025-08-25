plugins {
    id("com.android.application") version "8.5.0"
    kotlin("android") version "1.9.24"
}
android {
    namespace = "org.coreengine.demo"
    compileSdk = 34
    defaultConfig {
        applicationId = "org.coreengine.demo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
    }
}
dependencies {
    implementation(project(":engine"))
}
