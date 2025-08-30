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

package org.coreengine.android.host

import android.util.Log
import org.coreengine.api.util.LogLevel
import org.coreengine.api.util.Logger

class AndroidLogLogger(
    override var level: LogLevel = LogLevel.VERBOSE,
    override var tag: String = "CoreEngine"
) : Logger {
    override fun log(level: LogLevel, message: String, t: Throwable?, tag: String?) {
        if (this.level.ordinal < level.ordinal) return
        val tg = tag?.let { "${this.tag} $it" } ?: this.tag
        when (level) {
            LogLevel.VERBOSE -> if (t == null) Log.v(tg, message) else Log.v(tg, message, t)
            LogLevel.DEBUG   -> if (t == null) Log.d(tg, message) else Log.d(tg, message, t)
            LogLevel.INFO    -> if (t == null) Log.i(tg, message) else Log.i(tg, message, t)
            LogLevel.WARN    -> if (t == null) Log.w(tg, message) else Log.w(tg, message, t)
            LogLevel.ERROR   -> if (t == null) Log.e(tg, message) else Log.e(tg, message, t)
            LogLevel.NONE    -> {}
        }
    }
}