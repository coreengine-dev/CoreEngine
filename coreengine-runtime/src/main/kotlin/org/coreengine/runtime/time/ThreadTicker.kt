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


package org.coreengine.runtime.time

import org.coreengine.api.time.Clock
import org.coreengine.api.time.Ticker

class ThreadTicker(private val clock: Clock) : Ticker {

    @Volatile private var thread: Thread? = null
    @Volatile override var isRunning: Boolean = false
        private set

    override fun start(loop: (dt: Float) -> Unit) {
        if (isRunning) return
        isRunning = true
        thread = Thread({
            try {
                while (isRunning) {
                    val dt = clock.tick()
                    loop(dt)
                    clock.sleepToNextFrame()
                }
            } finally {
                isRunning = false
            }
        }, "CoreEngine-Loop").apply {
            isDaemon = true
            priority = Thread.NORM_PRIORITY
            start()
        }
    }

    override fun stop() {
        isRunning = false
        thread?.let { t ->
            try { t.join(2000) } finally {
                if (t.isAlive) t.interrupt()
            }
        }
        thread = null
    }
}



/*
package org.coreengine.runtime.time

import org.coreengine.api.time.Clock
import org.coreengine.api.time.Ticker

class ThreadTicker(private val clock: Clock) : Ticker {
    @Volatile private var thread: Thread? = null
    @Volatile override var isRunning: Boolean = false
        private set

    override fun start(cb: TickCallback) {
        if (isRunning) return
        isRunning = true
        thread = Thread({
            while (isRunning) {
                val dt = clock.tick()
                cb.onTick(dt)
                clock.sleepToNextFrame()
            }
        }, "CoreEngine-Loop").apply {
            isDaemon = true
            priority = Thread.NORM_PRIORITY
            start()
        }
    }

    override fun stop() {
        isRunning = false
        thread?.let { t -> t.join(2000); if (t.isAlive) t.interrupt() }
        thread = null
    }
}

 */