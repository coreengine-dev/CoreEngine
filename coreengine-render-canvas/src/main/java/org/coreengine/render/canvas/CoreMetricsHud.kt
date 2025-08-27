package org.coreengine.render.canvas

import android.graphics.Paint
import org.coreengine.camera.Camera
import org.coreengine.engine.metrics.FrameMetrics
import org.coreengine.hud.HudLayer
import org.coreengine.hud.MetricsSink
import org.coreengine.render.Renderer

/**
 * HUD de métricas para backend Canvas.
 * No mide tiempo. Solo muestra valores provistos por el engine.
 */
class CoreMetricsHud(
    private val overlay: MetricsOverlayCanvas
) : HudLayer(), FpsHud, MetricsSink {

    private val paint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        textSize = 34f
        isAntiAlias = true
    }

    @Volatile private var fps: Int = 0
    @Volatile private var lastMsUpdate: Float = 0f
    @Volatile private var lastMsRender: Float = 0f
    @Volatile private var lastDraws: Int = 0
    @Volatile private var lastAllocKb: Int = 0

    // FpsHud
    override fun setFps(v: Float) {
        fps = v.toInt()
        pushToOverlay()
    }

    // MetricsSink (contrato neutral del core)
    override fun onTimings(msUpdate: Float, msRender: Float, drawCalls: Int) {
        lastMsUpdate = msUpdate
        lastMsRender = msRender
        lastDraws = drawCalls
        pushToOverlay()
    }

    /** Permite actualizar el HUD con memoria si el host la conoce. */
    fun setAllocKb(kb: Int) {
        lastAllocKb = kb
        pushToOverlay()
    }

    private fun pushToOverlay() {
        overlay.update(
            FrameMetrics(
                frame = 0L,
                dt = 0f,
                msUpdate = lastMsUpdate,
                msRender = lastMsRender,
                drawCalls = lastDraws,
                allocKb = lastAllocKb,
                fps = fps
            )
        )
    }

    override fun onDraw(r: Renderer, c: Camera) {
        val cr = r as? CanvasRenderer ?: return
        overlay.draw(cr, paint, 8f, 28f)
    }
}
