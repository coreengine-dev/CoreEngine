import kotlinx.validation.KotlinApiCompareTask


plugins { alias(libs.plugins.kotlin.jvm) }

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}


        project(":coreengine-api") {
            tasks.named("apiCheck").configure {
                dependsOn(tasks.named("apiDump"))
            }
            // extra por si cambia el nombre del tipo de tarea
            tasks.withType<KotlinApiCompareTask>().configureEach {
                dependsOn(tasks.named("apiDump"))
            }
        }


dependencies { implementation(kotlin("stdlib")) }



//    ./gradlew :coreengine-api:apiCheck
//# si falla por cambios intencionales:
//./gradlew :coreengine-api:apiDump && git add coreengine-api/api/*.api
