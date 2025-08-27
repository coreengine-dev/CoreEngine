// JVM/Tests
package org.coreengine.util

class ConsoleLogger(
    override var level: LogLevel = LogLevel.VERBOSE,
    override var tag: String = "CoreEngine"
) : Logger {
    override fun log(level: LogLevel, message: String, t: Throwable?, tag: String?) {
        if (this.level.ordinal < level.ordinal) return
        val tg = tag?.let { "${this.tag} $it" } ?: this.tag
        val line = "[$tg][${level.name}] $message"
        if (level.ordinal >= LogLevel.WARN.ordinal) {
            System.err.println(line); t?.printStackTrace()
        } else {
            println(line); t?.printStackTrace()
        }
    }
}
