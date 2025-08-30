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

// coreengine/util/Debug.kt
package org.coreengine.runtime.util

import org.coreengine.api.util.LogLevel
import org.coreengine.api.util.Logger

object Debug {
    @Volatile var isEnabled: Boolean = true
    @Volatile var user: String = ""
    @Volatile var logger: Logger = ConsoleLogger() // por defecto en tests/JVM

    var level: LogLevel
        get() = logger.level
        set(v) { logger.level = v }

    var tag: String
        get() = logger.tag
        set(v) { logger.tag = v }

    // Facade simple
    fun log(level: LogLevel, msg: String, t: Throwable? = null, tag: String? = null) {
        if (!isEnabled || level == LogLevel.NONE) return
        logger.log(level, msg, t, tag)
    }

    // Helpers
    fun v(msg: String, t: Throwable? = null, tag: String? = null) = log(LogLevel.VERBOSE, msg, t, tag)
    fun d(msg: String, t: Throwable? = null, tag: String? = null) = log(LogLevel.DEBUG,   msg, t, tag)
    fun i(msg: String, t: Throwable? = null, tag: String? = null) = log(LogLevel.INFO,    msg, t, tag)
    fun w(msg: String, t: Throwable? = null, tag: String? = null) = log(LogLevel.WARN,    msg, t, tag)
    fun e(msg: String, t: Throwable? = null, tag: String? = null) = log(LogLevel.ERROR,   msg, t, tag)

    // Filtros por “user” (si te sirve)
    fun dUser(msg: String, u: String) { if (user == u) d(msg) }
    // … (añade variantes si de verdad las usas)
}
