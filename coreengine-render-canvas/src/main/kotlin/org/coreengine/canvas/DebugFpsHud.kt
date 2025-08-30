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

package org.coreengine.canvas

import android.graphics.Paint
import org.coreengine.api.render.Renderer

// time/FrameStats ya existe
// scene/DebugFpsHud.kt

interface FpsHud {
    fun setFps(v: Float)
}

// hud/DebugFpsHud.kt  (Android-only)
/*class DebugFpsHud(
    private val bootstrap: EngineBootstrap,              // referencia al motor de métricas
    private val overlay: MetricsOverlayCanvas            // overlay preparado para texto Canvas
) : HudLayer(), FpsHud
{

    override var visible: Boolean = true
    override var zIndex: Int = Int.MAX_VALUE

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

    override fun onDraw(r: Renderer) {
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
}*/

