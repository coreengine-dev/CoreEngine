package org.coreengine.render.canvas.ui


import android.graphics.Color
import org.coreengine.entity.Entity
import org.coreengine.camera.Camera
import org.coreengine.render.Renderer
import org.coreengine.render.canvas.CanvasRenderer
import org.coreengine.render.canvas.PaintCache

// core/ui/ButtonRect.kt
class ButtonRect(
    px: Float, py: Float, w: Float, h: Float,
    var text: String = "Button",
    var bgColor: Int = Color.WHITE,
    var fgColor: Int = Color.BLACK,
) : Entity() {

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
