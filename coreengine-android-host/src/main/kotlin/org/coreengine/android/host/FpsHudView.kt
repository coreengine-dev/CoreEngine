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

package org.coreengine.android.host


import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import org.coreengine.runtime.engine.EngineController.FrameSample
import org.coreengine.runtime.util.Debug

class FpsHudView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatTextView(ctx, attrs, defStyle) {

    init {
        setPadding(12, 8, 12, 8)
        textSize = 12f
        setTextColor(0x66000000)
        setBackgroundColor(0x11FF0000)
        // FpsHudView.init
        text = "FPS 0  draws 0  upd 0ms  ren 0ms"
        textSize = 14f
        setTextColor(0xFFFFFFFF.toInt())
        setBackgroundColor(0x99000000.toInt())

    }
    fun update(s: FrameSample) {
        text = "FPS ${s.fps}  draws ${s.draws}  upd ${s.msUpdate}ms  ren ${s.msRender}ms"
        Debug.i(text.toString())
    }
}
