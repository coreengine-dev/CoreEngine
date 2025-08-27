package org.coreengine.android.host

import android.util.Log
import org.coreengine.util.LogLevel
import org.coreengine.util.Logger

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