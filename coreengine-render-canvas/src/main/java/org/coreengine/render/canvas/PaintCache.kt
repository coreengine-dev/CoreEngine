package org.coreengine.render.canvas

import android.graphics.Paint

object PaintCache {
    private val map = HashMap<Int, Paint>()
    fun solid(argb: Int, textSize: Float = 0f, aa: Boolean = true): Paint {
        val key = 31 * argb + (textSize.toInt() shl 1) + if (aa) 1 else 0
        return map.getOrPut(key) {
            Paint().apply {
                color = argb
                isAntiAlias = aa
                if (textSize > 0f) this.textSize = textSize
            }
        }
    }
}