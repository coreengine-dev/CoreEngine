// ─────────────────────────────────────────────────
// Archivo: org/coreengine/engine/metrics/FrameSampler.kt
// Rol TCD: 𝛂 (acumulador, frecuencia efectiva)
// ─────────────────────────────────────────────────
package org.coreengine.engine.metrics

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
