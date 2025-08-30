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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import org.coreengine.api.camera.Camera
import org.coreengine.api.camera.Origin
import org.coreengine.api.entity.Entity
import org.coreengine.api.render.Renderer

// :coreengine-runtime/render/canvas/CanvasRenderer.kt
class CanvasRenderer : Renderer {
    @Volatile private var holder: SurfaceHolder? = null
    private var canvas: Canvas? = null
    private var draws = 0
    override val drawCallsThisFrame get() = draws

    fun attach(h: SurfaceHolder) { holder = h }
    fun withCanvas(block: (Canvas) -> Unit) { canvas?.let(block) }
    private inline fun markDraw(block: () -> Unit) { draws++; block() }

    override fun begin(camera: Camera) {
        draws = 0
        val c = holder?.lockCanvas() ?: run { canvas = null; return }
        canvas = c
        c.save()
        // Phase 1: sin transformaciones de cámara.
        // Solo limpiar para ver contenido y no romper overlay.
        c.drawColor(Color.BLACK) // o usa clear(...) desde Engine
    }

    override fun draw(entity: Entity) {
        // entity.onDraw(this) si tu Entity acepta Renderer
        entity.onDraw(this)
    }

    override fun clear(r: Float, g: Float, b: Float, a: Float) {
        val c = canvas ?: return
        c.drawColor(Color.argb((a*255).toInt(), (r*255).toInt(), (g*255).toInt(), (b*255).toInt()))
    }

    override fun end() {
        val c = canvas ?: return
        try {
            c.restore()
            holder?.unlockCanvasAndPost(c)
        } finally {
            canvas = null
        }
    }

    // Helpers
    fun drawRect(x: Float, y: Float, w: Float, h: Float, paint: Paint) {
        val c = canvas ?: return; markDraw { c.drawRect(x, y, x + w, y + h, paint) }
    }
    fun drawText(text: String, x: Float, y: Float, paint: Paint) {
        val c = canvas ?: return; markDraw { c.drawText(text, x, y, paint) }
    }
    fun drawCircle(cx: Float, cy: Float, r: Float, paint: Paint) {
        val c = canvas ?: return; markDraw { c.drawCircle(cx, cy, r, paint) }
    }
}


