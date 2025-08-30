plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "org.coreengine.android.host"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }

}

dependencies {
    implementation(project(":coreengine-api"))           // contratos
    implementation(project(":coreengine-runtime"))       // CoreEngine, EngineController
    implementation(project(":coreengine-render-canvas")) // CanvasRenderer (attach(holder))
    implementation(project(":coreengine-integration"))   // HostBridge, UiOverlay

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.junit.ktx)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.espresso.core)
}
