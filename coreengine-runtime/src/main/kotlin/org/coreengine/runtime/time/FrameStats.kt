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

package api.coreengine.runtime.time

// org.coreengine.time.FrameStats
/*class FrameStats {
    var fps = 0f; private var acc = 0f; private var frames = 0
    fun step(dt: Float): Float {
        acc += dt; frames++
        if (acc >= 1f) { fps = frames / acc; acc = 0f; frames = 0 }
        return fps
    }*/

class FrameStats {
    var fps = 0f; private set
    private var inited = false
    private val tau = 0.5f // ~ventana 0.5 s

    fun step(dt: Float): Float {
        val inst = if (dt > 0f) 1f / dt else 0f
        val alpha = 1f - kotlin.math.exp(-dt / tau)
        fps = if (!inited) { inited = true; inst } else { fps + alpha * (inst - fps) }
        return fps
    }



    /** Último valor conocido de FPS sin tocar contadores */
    fun fpsHint(): Float = fps
}