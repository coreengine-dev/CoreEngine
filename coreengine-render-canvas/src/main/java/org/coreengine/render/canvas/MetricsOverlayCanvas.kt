package org.coreengine.render.canvas

import android.graphics.Paint
import org.coreengine.engine.metrics.FrameMetrics

class MetricsOverlayCanvas {
    enum class Level { GREEN, YELLOW, RED }

    @Volatile
    private var line1 = ""
    @Volatile
    private var line2 = ""

    private val sb = StringBuilder(64)
    fun update(m: FrameMetrics, budgetMs: Float = 16.7f) {
        sb.setLength(0)
        sb.append("FPS ").append(m.fps)
            .append(" | U ").append(m.msUpdate.toInt()).append("ms")
            .append(" | R ").append(m.msRender.toInt()).append("ms")
        line1 = sb.toString()
        sb.setLength(0)
        sb.append("Draw ").append(m.drawCalls).append(" | Heap ").append(m.allocKb).append(" KB")
        line2 = sb.toString()
    }


    fun draw(r: CanvasRenderer, paint: Paint, x: Float, y: Float) {
        r.drawText(line1, x, y, paint)
        r.drawText(line2, x, y + paint.textSize + 4f, paint)
    }
}