import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.intellij.platform")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    // Dependencia al propio engine
    implementation(project(":coreengine-api"))
    implementation(project(":coreengine-runtime"))

    // Dependencia a la plataforma IntelliJ
    intellijPlatform {
        intellijIdeaCommunity("2024.2")
//        bundledPlugins("com.intellij.java") // ej. añadir soporte Java
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

// --- Binary Compatibility Validator ---
val apiBaseline = layout.projectDirectory.file("api/plugin.api")

tasks.matching { it.name in listOf("apiCheck","apiDump") }
    .configureEach { enabled = false }


tasks.register("updateApi") {
    group = "verification"
    description = "Actualiza el baseline de API pública (apiDump)."
    dependsOn("apiDump")
}


//    ./gradlew --stop
//    ./gradlew clean :ide:plugin:build
//    ./gradlew :ide:plugin:runIde