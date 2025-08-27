package org.coreengine.render.canvas

import android.graphics.Paint
import org.coreengine.camera.Camera
import org.coreengine.engine.EngineBootstrap
import org.coreengine.hud.HudLayer
import org.coreengine.render.Renderer

// time/FrameStats ya existe
// scene/DebugFpsHud.kt

interface FpsHud {
    fun setFps(v: Float)
}

// hud/DebugFpsHud.kt  (Android-only)
class DebugFpsHud(
    private val bootstrap: EngineBootstrap,              // referencia al motor de métricas
    private val overlay: MetricsOverlayCanvas            // overlay preparado para texto Canvas
) : HudLayer(), FpsHud {

    private val paint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        textSize = 34f
        isAntiAlias = true
    }

    @Volatile private var fps = 0f
    private var lastNs = System.nanoTime()

    override fun setFps(v: Float) {
        fps = v
    }

    override fun onDraw(r: Renderer, c: Camera) {
        val canvasRenderer = r as? CanvasRenderer ?: return

        // calcular delta time
        val now = System.nanoTime()
        val dt = ((now - lastNs).coerceAtLeast(0)) / 1_000_000_000f
        lastNs = now

        // actualizar métricas
        bootstrap.tick(dt)

        // texto principal FPS
        canvasRenderer.drawText("FPS: ${"%.1f".format(fps)}", 8f, 28f, paint)

        // métricas extendidas
        overlay.draw(canvasRenderer, paint, 8f, 28f + paint.textSize + 6f)
    }
}

