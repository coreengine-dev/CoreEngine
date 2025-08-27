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

package org.coreengine.engine

import org.coreengine.time.*

class EngineController internal constructor(
    private val engine: CoreEngine,
    private val tickerFactory: (Clock) -> Ticker = { c -> ThreadTicker(c) } // default JVM
) {
    @Volatile private var ticker: Ticker? = null

    @Synchronized fun start() {
        if (ticker?.isRunning == true) return
        engine.start()
        val t = tickerFactory(engine.clock)
        t.start { dt -> engine.tickFrame(dt) }
        ticker = t
    }

    @Synchronized fun stop() {
        ticker?.stop()
        ticker = null
        engine.stop()
    }

    fun pause()  = engine.pause()
    fun resume() = engine.resume()
    val isAlive: Boolean get() = ticker?.isRunning == true
}
