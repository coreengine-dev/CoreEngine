// ─────────────────────────────────────────────────
// Archivo: org/coreengine/scene/BudgetEnforcer.kt
// Rol TCD: 𝝮→𝝎 (control + señal de eventos)
// No modifica tu Scene. Se invoca desde el loop del Engine/AppController.
// ─────────────────────────────────────────────────
package org.coreengine.scene

class BudgetEnforcer(val manifest: SceneManifest) {
    private var overFrames = 0

    data class Breach(val kind: String, val value: Int, val limit: Int)

    fun checkDrawCalls(drawCalls: Int): Breach? =
        if (drawCalls > manifest.maxSprites) Breach("DrawCalls", drawCalls, manifest.maxSprites) else null

    fun checkTextChars(chars: Int): Breach? =
        if (chars > manifest.maxTextChars) Breach("TextChars", chars, manifest.maxTextChars) else null

    /** @return true si amerita throttle tras N frames fuera de presupuesto. */
    fun checkFrameTime(msTotal: Float, consecutiveLimit: Int = 5): Boolean {
        if (msTotal > manifest.targetMsPerFrame) overFrames++ else overFrames = 0
        return overFrames >= consecutiveLimit
    }

    fun reset() { overFrames = 0 }
}