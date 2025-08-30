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
import org.coreengine.api.camera.Camera
import org.coreengine.api.hud.HudLayer
import org.coreengine.api.render.Renderer
import org.coreengine.canvas.hud.MetricsSink

/**
 * HUD de métricas para backend Canvas.
 * No mide tiempo. Solo muestra valores provistos por el engine.
 */
/*class CoreMetricsHud(
    private val overlay: MetricsOverlayCanvas
) : HudLayer, FpsHud, MetricsSink
{

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

    *//** Permite actualizar el HUD con memoria si el host la conoce. *//*
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

    override var visible: Boolean = true
    override var zIndex: Int = Int.MAX_VALUE

    override fun onDraw(r: Renderer, c: Camera) {
        val cr = r as? CanvasRenderer ?: return
        overlay.draw(cr, paint, 8f, 28f)
    }
}*/
