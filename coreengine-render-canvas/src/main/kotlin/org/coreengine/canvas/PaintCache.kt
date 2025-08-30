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