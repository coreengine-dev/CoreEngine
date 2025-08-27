// ─────────────────────────────────────────────────
// Archivo: org/coreengine/engine/metrics/FrameMetrics.kt
// Rol TCD: 𝛂 (medición de dt) + 𝝎 (snapshot del tick)
// ─────────────────────────────────────────────────
package org.coreengine.engine.metrics

data class FrameMetrics(
    val frame: Long,
    val dt: Float,
    val msUpdate: Float,
    val msRender: Float,
    val drawCalls: Int,
    val allocKb: Int,
    val fps: Int
)