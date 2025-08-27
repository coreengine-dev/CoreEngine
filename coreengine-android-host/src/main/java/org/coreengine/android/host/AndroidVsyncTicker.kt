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

import android.view.Choreographer
import org.coreengine.time.TickCallback
import org.coreengine.time.Ticker

class AndroidVsyncTicker : Ticker, Choreographer.FrameCallback {
    private val ch = Choreographer.getInstance()
    private var last = 0L
    private var cb: TickCallback? = null
    @Volatile override var isRunning: Boolean = false
        private set

    override fun start(cb: TickCallback) {
        if (isRunning) return
        this.cb = cb
        isRunning = true
        last = 0L
        ch.postFrameCallback(this)
    }

    override fun doFrame(t: Long) {
        if (!isRunning) return
        if (last == 0L) last = t
        val dt = ((t - last) / 1_000_000_000.0).toFloat().coerceAtMost(0.1f)
        last = t
        cb?.onTick(dt)
        ch.postFrameCallback(this)
    }

    override fun stop() {
        if (!isRunning) return
        isRunning = false
        ch.removeFrameCallback(this)
        cb = null
    }
}
