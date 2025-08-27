plugins {
    kotlin("jvm")    // sin version aquí
}
java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies { testImplementation(kotlin("test")) }
tasks.test { useJUnitPlatform() }

