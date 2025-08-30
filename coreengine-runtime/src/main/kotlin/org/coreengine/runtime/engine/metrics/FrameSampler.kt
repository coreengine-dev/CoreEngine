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

// ─────────────────────────────────────────────────
// Archivo: org/coreengine/engine/metrics/FrameSampler.kt
// Rol TCD: 𝛂 (acumulador, frecuencia efectiva)
// ─────────────────────────────────────────────────
package api.coreengine.runtime.engine.metrics

class FrameSampler(private val targetFps: Int) {
    private var frame = 0L
    private var accum = 0f
    private var framesInSec = 0
    private var fps = targetFps

    fun sample(dt: Float, msUpdate: Float, msRender: Float, drawCalls: Int, allocBytes: Long): FrameMetrics {
        frame++
        accum += dt
        framesInSec++
        if (accum >= 1f) { fps = framesInSec; accum -= 1f; framesInSec = 0 }
        return FrameMetrics(
            frame, dt, msUpdate, msRender, drawCalls,
            (allocBytes / 1024).toInt(), fps
        )
    }
}
