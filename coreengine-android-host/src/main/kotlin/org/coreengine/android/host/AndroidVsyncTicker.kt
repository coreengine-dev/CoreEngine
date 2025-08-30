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

import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import org.coreengine.api.time.Ticker
import org.coreengine.runtime.time.TickCallback

class AndroidVsyncTicker : Ticker, Choreographer.FrameCallback {
    @Volatile override var isRunning: Boolean = false
        private set

    private var onTick: ((Float) -> Unit)? = null
    private var lastNs = 0L

    override fun start(loop: (Float) -> Unit) {
        if (isRunning) return
        isRunning = true
        onTick = loop
        lastNs = 0L
        if (Looper.myLooper() == Looper.getMainLooper())
            Choreographer.getInstance().postFrameCallback(this)
        else
            Handler(Looper.getMainLooper()).post {
                Choreographer.getInstance().postFrameCallback(this)
            }
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isRunning) return
        val dt = if (lastNs == 0L) 1f/60f
        else ((frameTimeNanos - lastNs) / 1_000_000_000f).coerceIn(1f/240f, 1f/30f)
        lastNs = frameTimeNanos
        onTick?.invoke(dt)
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun stop() {
        if (!isRunning) return
        isRunning = false
        onTick = null
        Choreographer.getInstance().removeFrameCallback(this)
    }
}

