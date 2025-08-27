package org.coreengine.util


/**
 * Logger simple para depuración y métricas.
 * TCD: 𝝎 (observación/medición).
 */

enum class LogLevel { NONE, ERROR, WARN, INFO, DEBUG, VERBOSE }

interface Logger {
    var level: LogLevel
    var tag: String
    fun log(level: LogLevel, message: String, t: Throwable? = null, tag: String? = null)
}
