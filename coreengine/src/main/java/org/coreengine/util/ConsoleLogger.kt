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
