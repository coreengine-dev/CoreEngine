plugins {
    kotlin("jvm") version "1.9.24"
}
java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }
dependencies {
    testImplementation(kotlin("test"))
}
tasks.test { useJUnitPlatform() }
