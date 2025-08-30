import java.nio.file.Paths


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"

}

subprojects {
    group = rootProject.group
    version = rootProject.version

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

subprojects {
    // aplica toolchain a todo módulo Kotlin
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(17)
        }
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.android") {
        extensions.configure<com.android.build.gradle.BaseExtension> {
            compileSdkVersion(36)
            defaultConfig { minSdk = 24 }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}

apiValidation {
    // Solo validar API pública del núcleo puro
    ignoredProjects.addAll(
        setOf(
            "coreengine-android-host",
            "coreengine-runtime",
            "coreengine-render-canvas",
            "coreengine-render-gl",
            "coreengine-integration",
            "samples",
            "demo-app"
        )
    )
    nonPublicMarkers += listOf("org.coreengine.InternalApi")
}


//********** Aplica licencia en la cabecera de todos los archivos ks***********
val licenseHeader = """
/*
 * Copyright 2025 Juan José Nicolini
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
""".trimIndent()

tasks.register("applyLicenseHeaders") {
    notCompatibleWithConfigurationCache("Uses script objects")
    doLast {
        fileTree(".") {
            include("**/*.kt")
            exclude("**/build/**", "**/.gradle/**")
        }.forEach { f ->
            val content = f.readText()
            if (!content.startsWith("/*\n * Copyright")) {
                f.writeText(licenseHeader + "\n\n" + content)
            }
        }
    }
}

// ./gradlew applyLicenseHeaders


//*******genera un archivo txt de cada mudulo que se encuntra el la aplicaión**********

tasks.register("kotlinInventoryChunked") {
    group = "reporting"
    description = "Inventario .kt por módulo, con código completo y división por tamaño"
    notCompatibleWithConfigurationCache("Recorre subprojects y fileTree en ejecución")

    doLast {
        val maxKb = (project.findProperty("inventoryMaxKb")?.toString()?.toIntOrNull() ?: 1024)

        val includeMods = (findProperty("inventoryIncludeMods") as String?)
            ?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()
        val excludeMods = (findProperty("inventoryExcludeMods") as String?)
            ?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()

        val includeRegex = (findProperty("inventoryIncludeRegex") as String?)?.let { Regex(it) }
        val excludeRegex = (findProperty("inventoryExcludeRegex") as String?)?.let { Regex(it) }
        val includeApps = (findProperty("inventoryIncludeApps")?.toString()?.toBoolean() ?: false)

        fun moduleAllowed(p: Project): Boolean {
            val path = p.path
            if (!includeApps && p.plugins.hasPlugin("com.android.application")) return false
            if (includeMods != null && path !in includeMods) return false
            if (includeRegex != null && !includeRegex.matches(path)) return false
            if (excludeMods != null && path in excludeMods) return false
            if (excludeRegex != null && excludeRegex.matches(path)) return false
            return true
        }

        val outDir = rootProject.layout.buildDirectory.dir("reports/kotlin-inventory").get().asFile
        outDir.mkdirs()


        val ktMatcher =
            Regex("""\b(?:(?:data|sealed|enum|annotation)\s+)?(class|interface|object)\s+([A-Za-z_][A-Za-z0-9_]*)""")
        val pkgMatcher = Regex("""^\s*package\s+([A-Za-z0-9_.]+)""")
        val impMatcher = Regex("""^\s*import\s+([A-Za-z0-9_.*]+)""")
        fun bytesOf(s: String) = s.toByteArray(Charsets.UTF_8).size
        fun moduleId(p: Project) = p.path.replace(':', '_').ifEmpty { "root" }

        rootProject.subprojects
            .filter(::moduleAllowed)
            .forEach { sub ->
                val tree = sub.fileTree(
                    mapOf(
                        "dir" to sub.projectDir,
                        "includes" to listOf("**/*.kt"),
                        "excludes" to listOf("**/build/**", "**/.gradle/**", "**/out/**")
                    )
                )

                val modId = moduleId(sub)
                var part = 1
                var buf = StringBuilder()
                var bufBytes = 0
                val limitBytes = maxKb * 1024

                fun flush() {
                    val fileOut = File(outDir, "${modId}-inventory.part%03d.txt".format(part))
                    fileOut.writeText(buf.toString())
                    println("Escrito: ${fileOut.absolutePath} (${bufBytes} bytes)")
                    part++
                    buf = StringBuilder()
                    bufBytes = 0
                }

                tree.files.sortedBy { it.absolutePath }.forEach { f ->
                    val rel = Paths.get(rootProject.projectDir.absolutePath)
                        .relativize(f.toPath()).toString()

                    val text = f.readText(Charsets.UTF_8)
                    val lines = text.lines()

                    val pkg = lines.firstOrNull { it.trimStart().startsWith("package ") }
                        ?.let { pkgMatcher.find(it)?.groupValues?.getOrNull(1) } ?: "(sin paquete)"
                    val imports =
                        lines.mapNotNull { impMatcher.find(it)?.groupValues?.getOrNull(1) }
                            .distinct().sorted()
                    val classes =
                        ktMatcher.findAll(text).map { it.groupValues[1] + " " + it.groupValues[2] }
                            .toList()

                    val block = buildString {
                        appendLine("MODULE: ${sub.path}")
                        appendLine("FILE: $rel")
                        appendLine("PACKAGE: $pkg")
                        appendLine("IMPORTS:")
                        if (imports.isEmpty()) appendLine("  (ninguno)") else imports.forEach {
                            appendLine(
                                "  - $it"
                            )
                        }
                        appendLine("CLASSES:")
                        if (classes.isEmpty()) appendLine("  (ninguna)") else classes.forEach {
                            appendLine(
                                "  - $it"
                            )
                        }
                        appendLine("SOURCE-BEGIN")
                        appendLine(text)
                        appendLine("SOURCE-END")
                        appendLine("-".repeat(80))
                    }

                    val blockBytes = bytesOf(block)
                    val limit = limitBytes

                    if (blockBytes > limit) {
                        // Corta el archivo en fragmentos de texto manteniendo encabezado y marcas
                        val header = buildString {
                            appendLine("MODULE: ${sub.path}")
                            appendLine("FILE: $rel")
                            appendLine("PACKAGE: $pkg")
                            appendLine("IMPORTS:")
                            if (imports.isEmpty()) appendLine("  (ninguno)") else imports.forEach {
                                appendLine(
                                    "  - $it"
                                )
                            }
                            appendLine("CLASSES:")
                            if (classes.isEmpty()) appendLine("  (ninguna)") else classes.forEach {
                                appendLine(
                                    "  - $it"
                                )
                            }
                            appendLine("SOURCE-BEGIN (chunked)")
                        }
                        val footer = "\nSOURCE-END\n" + "-".repeat(80) + "\n"

                        val hdrBytes = bytesOf(header)
                        val ftrBytes = bytesOf(footer)
                        val room = limit - hdrBytes - ftrBytes
                        val src = text.toByteArray(Charsets.UTF_8)
                        var off = 0
                        while (off < src.size) {
                            if (bufBytes > 0 && bufBytes + hdrBytes > limit) flush()
                            buf.append(header); bufBytes += hdrBytes

                            val take = minOf(room, src.size - off)
                            val chunkStr = String(src, off, take, Charsets.UTF_8)
                            buf.append(chunkStr); bufBytes += bytesOf(chunkStr)
                            off += take

                            buf.append(footer); bufBytes += ftrBytes
                            if (bufBytes >= limit) flush()
                        }
                    } else {
                        if (bufBytes + blockBytes > limit && bufBytes > 0) flush()
                        buf.append(block); bufBytes += blockBytes
                    }
                }

                if (bufBytes > 0) flush()
                if (part == 1) {
                    val fileOut = File(outDir, "${modId}-inventory.part001.txt")
                    fileOut.writeText("MODULE: ${sub.path}\n(sin archivos .kt)\n")
                    println("Escrito: ${fileOut.absolutePath} (módulo sin .kt)")
                }
            }

        println("Salida: ${outDir.absolutePath}")
        println("Límite por parte: $maxKb KB")
    }
}

//            ./gradlew --no-configuration-cache kotlinInventoryChunked -PinventoryMaxKb=512
//# filtros opcionales:
//# -PinventoryExcludeMods=:app
//# -PinventoryIncludeMods=:coreengine,:engine
//# -PinventoryIncludeRegex="^:(coreengine|engine)(:.*)?$"
//# -PinventoryIncludeApps=true


//    ./gradlew clean :coreengine-runtime:test
//    ./gradlew :samples:demo-app:assembleDebug
//    ./gradlew build
