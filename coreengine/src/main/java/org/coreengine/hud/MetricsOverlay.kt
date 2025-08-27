// ─────────────────────────────────────────────────
// Archivo: org/coreengine/hud/MetricsOverlay.kt
// Rol TCD: 𝐌 (render de estado) + 𝝎 (alertas visuales)
// NOTA: Usa TU clase Text exacta. No creamos otra.
// ─────────────────────────────────────────────────
package org.coreengine.hud

import org.coreengine.engine.metrics.FrameMetrics


class Text(){
    fun setCharactersToDraw(length: Int) {}
    lateinit var text: String
    val charactersMaximum: Int get() = 0
}

class MetricsOverlay(private val textNode: Text) {
    enum class Level { GREEN, YELLOW, RED }
    var enabled: Boolean = true

    fun update(m: FrameMetrics, budgetMs: Float = 16.7f) {
        if (!enabled) return
        val level = when {
            m.msUpdate + m.msRender <= budgetMs     -> Level.GREEN
            m.msUpdate + m.msRender <= budgetMs*1.5 -> Level.YELLOW
            else                                    -> Level.RED
        }

        val raw = "FPS ${m.fps} | U ${"%.2f".format(m.msUpdate)}ms | R ${"%.2f".format(m.msRender)}ms\n" +
                "Draw ${m.drawCalls} | Heap ${m.allocKb} KB | ${level.name}"

        val cap = textNode.charactersMaximum
        val safe = if (raw.length > cap) raw.take(cap) else raw

        textNode.text = safe
        textNode.setCharactersToDraw(safe.length)
    }

}