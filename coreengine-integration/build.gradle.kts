plugins {
    alias(libs.plugins.kotlin.jvm)
}
kotlin { jvmToolchain(17) }
dependencies {
    api(project(":coreengine-api")) // si expones tipos del API
    // nada de AndroidX ni Compose aquí
}
