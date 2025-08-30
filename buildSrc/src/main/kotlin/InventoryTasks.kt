// buildSrc/src/main/kotlin/InventoryTasks.kt
@file:Suppress("UnstableApiUsage")

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Paths

open class KotlinInventoryChunkedTask : DefaultTask() {

    @Input @Optional var inventoryMaxKb: Int = (project.findProperty("inventoryMaxKb")?.toString()?.toIntOrNull() ?: 1024)
    @Input @Optional var includeMods: Set<String>? =
        (project.findProperty("inventoryIncludeMods") as String?)?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()
    @Input @Optional var excludeMods: Set<String>? =
        (project.findProperty("inventoryExcludeMods") as String?)?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()
    @Input @Optional var includeRegex: Regex? =
        (project.findProperty("inventoryIncludeRegex") as String?)?.let { Regex(it) }
    @Input @Optional var excludeRegex: Regex? =
        (project.findProperty("inventoryExcludeRegex") as String?)?.let { Regex(it) }
    @Input @Optional var includeApps: Boolean =
        (project.findProperty("inventoryIncludeApps")?.toString()?.toBoolean() ?: false)

    private val ktMatcher = Regex("""\b(?:(?:data|sealed|enum|annotation)\s+)?(class|interface|object)\s+([A-Za-z_][A-Za-z0-9_]*)""")
    private val pkgMatcher = Regex("""^\s*package\s+([A-Za-z0-9_.]+)""", RegexOption.MULTILINE)
    private val impMatcher = Regex("""^\s*import\s+([A-Za-z0-9_.*]+)""", RegexOption.MULTILINE)
    private val pubTypeMatcher = Regex("""\b(public\s+)?(data\s+)?(class|interface|object|enum\s+class|sealed\s+class)\s+([A-Za-z_][A-Za-z0-9_]*)""", RegexOption.MULTILINE)

    private fun bytesOf(s: String) = s.toByteArray(Charsets.UTF_8).size
    private fun moduleId(p: Project) = p.path.replace(':', '_').ifEmpty { "root" }

    private fun moduleAllowed(p: Project): Boolean {
        val path = p.path
        if (!includeApps && p.plugins.hasPlugin("com.android.application")) return false
        if (includeMods != null && path !in includeMods!!) return false
        if (includeRegex != null && !includeRegex!!.matches(path)) return false
        if (excludeMods != null && path in excludeMods!!) return false
        if (excludeRegex != null && excludeRegex!!.matches(path)) return false
        return true
    }

    @TaskAction
    fun run() {
        val outDir = project.rootProject.layout.buildDirectory.dir("reports/kotlin-inventory").get().asFile
        outDir.mkdirs()
        val limitBytes = inventoryMaxKb * 1024

        // Acumuladores para PROJECT_TREE, api_surface e inventory.json
        data class PkgInfo(var files: Int = 0, val types: MutableSet<String> = linkedSetOf())
        data class ModInfo(val name: String,
                           val packages: MutableMap<String, PkgInfo> = linkedMapOf(),
                           var files: Int = 0, var kotlin: Int = 0, var java: Int = 0, var glsl: Int = 0, var lines: Int = 0)

        val allMods = mutableListOf<ModInfo>()

        project.rootProject.subprojects
            .filter(::moduleAllowed)
            .sortedBy { it.path }
            .forEach { sub ->
                val modInfo = ModInfo(sub.path)
                allMods += modInfo

                val tree = sub.fileTree(
                    mapOf(
                        "dir" to sub.projectDir,
                        "includes" to listOf("**/*.kt", "**/*.java", "**/*.glsl", "**/*.vert", "**/*.frag"),
                        "excludes" to listOf("**/build/**", "**/.gradle/**", "**/out/**")
                    )
                )

                val modId = moduleId(sub)
                var part = 1
                var buf = StringBuilder()
                var bufBytes = 0

                fun flush() {
                    val fileOut = File(outDir, "${modId}-inventory.part%03d.txt".format(part))
                    fileOut.writeText(buf.toString())
                    println("Escrito: ${fileOut.absolutePath} (${bufBytes} bytes)")
                    part++
                    buf = StringBuilder()
                    bufBytes = 0
                }

                tree.files.sortedBy { it.absolutePath }.forEach { f ->
                    val ext = f.extension.lowercase()
                    modInfo.files++

                    val rel = Paths.get(project.rootProject.projectDir.absolutePath).relativize(f.toPath()).toString()
                    val text = try { f.readText(Charsets.UTF_8) } catch (_: Exception) { "" }
                    val linesCount = if (text.isEmpty()) 0 else text.count { it == '\n' } + 1
                    modInfo.lines += linesCount

                    when (ext) {
                        "kt" -> modInfo.kotlin++
                        "java" -> modInfo.java++
                        "glsl", "vert", "frag" -> modInfo.glsl++
                    }

                    if (ext == "kt") {
                        val pkg = pkgMatcher.find(text)?.groupValues?.getOrNull(1) ?: "(sin.paquete)"
                        val imports = impMatcher.findAll(text).map { it.groupValues[1] }.toSet().sorted()
                        val classes = ktMatcher.findAll(text).map { it.groupValues[1] + " " + it.groupValues[2] }.toList()

                        // Public API surface para summaries
                        val publicTypes = pubTypeMatcher.findAll(text).map { it.groupValues[4] }.toList()
                        val bucket = modInfo.packages.getOrPut(pkg) { PkgInfo() }
                        bucket.files += 1
                        bucket.types.addAll(publicTypes)

                        val block = buildString {
                            appendLine("MODULE: ${sub.path}")
                            appendLine("FILE: $rel")
                            appendLine("PACKAGE: $pkg")
                            appendLine("IMPORTS:")
                            if (imports.isEmpty()) appendLine("  (ninguno)") else imports.forEach { appendLine("  - $it") }
                            appendLine("CLASSES:")
                            if (classes.isEmpty()) appendLine("  (ninguna)") else classes.forEach { appendLine("  - $it") }
                            appendLine("SOURCE-BEGIN")
                            appendLine(text)
                            appendLine("SOURCE-END")
                            appendLine("-".repeat(80))
                        }

                        val blockBytes = bytesOf(block)
                        if (blockBytes > limitBytes) {
                            val header = buildString {
                                appendLine("MODULE: ${sub.path}")
                                appendLine("FILE: $rel")
                                appendLine("PACKAGE: $pkg")
                                appendLine("IMPORTS:")
                                if (imports.isEmpty()) appendLine("  (ninguno)") else imports.forEach { appendLine("  - $it") }
                                appendLine("CLASSES:")
                                if (classes.isEmpty()) appendLine("  (ninguna)") else classes.forEach { appendLine("  - $it") }
                                appendLine("SOURCE-BEGIN (chunked)")
                            }
                            val footer = "\nSOURCE-END\n" + "-".repeat(80) + "\n"

                            val hdrBytes = bytesOf(header)
                            val ftrBytes = bytesOf(footer)
                            val room = limitBytes - hdrBytes - ftrBytes
                            val src = text.toByteArray(Charsets.UTF_8)
                            var off = 0
                            while (off < src.size) {
                                if (bufBytes > 0 && bufBytes + hdrBytes > limitBytes) flush()
                                buf.append(header); bufBytes += hdrBytes

                                val take = minOf(room, src.size - off)
                                val chunkStr = String(src, off, take, Charsets.UTF_8)
                                buf.append(chunkStr); bufBytes += bytesOf(chunkStr)
                                off += take

                                buf.append(footer); bufBytes += ftrBytes
                                if (bufBytes >= limitBytes) flush()
                            }
                        } else {
                            if (bufBytes + blockBytes > limitBytes && bufBytes > 0) flush()
                            buf.append(block); bufBytes += blockBytes
                        }
                    }
                }

                if (bufBytes > 0) flush()
                if (part == 1) {
                    val fileOut = File(outDir, "${modId}-inventory.part001.txt")
                    fileOut.writeText("MODULE: ${sub.path}\n(sin archivos .kt)\n")
                    println("Escrito: ${fileOut.absolutePath} (módulo sin .kt)")
                }
            }

        // PROJECT_TREE.md
        File(outDir, "PROJECT_TREE.md").writeText(buildString {
            appendLine("# PROJECT_TREE.md\n")
            allMods.forEach { m ->
                appendLine("- **${m.name}** — files:${m.files} kt:${m.kotlin} glsl:${m.glsl} lines:${m.lines}")
                m.packages.toSortedMap().forEach { (pkg, info) ->
                    val types = if (info.types.isEmpty()) "" else " — " + info.types.sorted().joinToString(", ")
                    appendLine("  - `$pkg` (${info.files} files)$types")
                }
            }
        })

        // api_surface.md
        File(outDir, "api_surface.md").writeText(buildString {
            appendLine("# api_surface.md\n")
            allMods.forEach { m ->
                val withTypes = m.packages.filterValues { it.types.isNotEmpty() }
                if (withTypes.isNotEmpty()) {
                    appendLine("## ${m.name}")
                    withTypes.toSortedMap().forEach { (pkg, info) ->
                        appendLine("- `$pkg`: ${info.types.sorted().joinToString(", ")}")
                    }
                    appendLine()
                }
            }
        })

        // inventory.json (resumen)
        File(outDir, "inventory.json").writeText(buildString {
            fun esc(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
            append("{\n  \"modules\": [\n")
            allMods.forEachIndexed { i, m ->
                append("    {\"name\":\"${esc(m.name)}\",\"files\":${m.files},\"kotlin\":${m.kotlin},\"java\":${m.java},\"glsl\":${m.glsl},\"lines\":${m.lines},\"packages\":{")
                append(m.packages.entries.joinToString(",") { e ->
                    val pkg = esc(e.key)
                    val v = e.value
                    "\"$pkg\":{\"files\":${v.files},\"types\":[${v.types.joinToString(",") { "\"${esc(it)}\"" }}]}"
                })
                append("}}"); if (i < allMods.lastIndex) append(","); append("\n")
            }
            append("  ]\n}\n")
        })

        println("Salida: ${outDir.absolutePath}")
        println("Límite por parte: $inventoryMaxKb KB")
    }
}

// Alias simple si lo quieres
open class KotlinInventoryTask : DefaultTask() {
    @TaskAction fun run() {
        project.tasks.named("kotlinInventoryChunked", KotlinInventoryChunkedTask::class.java).get().run()
    }
}

tasks.register("kotlinInventoryChunked", KotlinInventoryChunkedTask::class.java) {
    group = "reporting"
    description = "Inventario .kt por módulo, con código completo, resumen API y archivos chunked"
}
tasks.register("kotlinInventory", KotlinInventoryTask::class.java) {
    group = "reporting"
    description = "Alias de kotlinInventoryChunked"
}
