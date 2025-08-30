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

/*
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
}*/
