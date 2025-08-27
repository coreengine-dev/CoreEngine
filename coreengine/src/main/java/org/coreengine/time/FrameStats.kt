package org.coreengine.time

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