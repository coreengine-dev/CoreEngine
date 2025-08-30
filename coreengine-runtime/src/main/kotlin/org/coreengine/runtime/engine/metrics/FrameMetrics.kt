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
// Archivo: org/coreengine/engine/metrics/FrameMetrics.kt
// Rol TCD: 𝛂 (medición de dt) + 𝝎 (snapshot del tick)
// ─────────────────────────────────────────────────
package api.coreengine.runtime.engine.metrics

data class FrameMetrics(
    val frame: Long,
    val dt: Float,
    val msUpdate: Float,
    val msRender: Float,
    val drawCalls: Int,
    val allocKb: Int,
    val fps: Int
)