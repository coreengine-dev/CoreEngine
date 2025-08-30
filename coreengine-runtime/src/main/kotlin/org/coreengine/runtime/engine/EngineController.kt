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


package org.coreengine.runtime.engine

import org.coreengine.api.time.Ticker
import org.coreengine.runtime.util.Debug

class EngineController(
    private val engine: CoreEngine,
    private val ticker: Ticker
) {
    val isRunning get() = ticker.isRunning
    var onSample: ((FrameSample) -> Unit)? = null

    fun start() {
        Debug.i("EngineController.start")
        engine.onControllerStart()
        ticker.start { dt -> loop(dt) }   // <-- antes llamabas engine.tickFrame(dt)
    }

    fun stop() {
        ticker.stop()
        engine.onControllerStop()
    }

    data class FrameSample(val fps: Int, val draws: Int, val msUpdate: Int, val msRender: Int)

    private var accT = 0f;
    private var accF = 0
    private var lastDraws = 0;
    private var msUpd = 0;
    private var msRen = 0

    private fun loop(dt: Float) {
        val t0 = System.nanoTime()
        engine.tickFrame(dt)
        msUpd = ((System.nanoTime() - t0) / 1_000_000).toInt()
        lastDraws = engine.renderer.drawCallsThisFrame
        accT += dt; accF++
        if (accT >= 1f) {
            onSample?.invoke(FrameSample(accF, lastDraws, msUpd, msRen))
            accT = 0f; accF = 0
        }
    }
}

