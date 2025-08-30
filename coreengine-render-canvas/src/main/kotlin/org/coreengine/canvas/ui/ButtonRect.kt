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

package org.coreengine.canvas.ui


import android.graphics.Color
import org.coreengine.api.camera.Camera
import org.coreengine.api.entity.Entity
import org.coreengine.api.render.Renderer
import org.coreengine.canvas.CanvasRenderer
import org.coreengine.canvas.PaintCache

// core/ui/ButtonRect.kt
class ButtonRect(
    px: Float, py: Float, w: Float, h: Float,
    var text: String = "Button",
    var bgColor: Int = Color.WHITE,
    var fgColor: Int = Color.BLACK,
){}/* : Entity() {

    private val bg = PaintCache.solid(bgColor).apply { isAntiAlias = true }
    private val fg = PaintCache.solid(fgColor).apply {
        textSize = 36f
        isAntiAlias = true
    }

    init {
        setBounds(px, py, w, h)
    }

    override fun onDraw(renderer: Renderer, camera: Camera) {

        val r = renderer as? CanvasRenderer ?: return
        r.drawRect(x, y, width, height, bg)
        r.drawText(text, x + 16f, y + height / 2 + 12f, fg)
        super.onDraw(renderer, camera)
    }
}

        */