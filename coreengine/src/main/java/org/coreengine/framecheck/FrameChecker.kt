// ─────────────────────────────────────────────────
// Archivo: org/coreengine/framecheck/FrameChecker.kt
// Rol TCD: 𝝮→𝝎 (observadores modulables tipo "checkers")
// ─────────────────────────────────────────────────
package org.coreengine.framecheck

import org.coreengine.state.EngineSnapshot

data class CheckerReport(val kind: String, val severity: Int, val details: String)

interface FrameChecker { fun onFrame(s: EngineSnapshot): List<CheckerReport> }

class MemBudgetChecker(private val kbLimit: Int): FrameChecker {
    override fun onFrame(s: EngineSnapshot): List<CheckerReport> {
        val kb = s.counters["heapKb"] ?: return emptyList()
        return if (kb > kbLimit) listOf(CheckerReport("MemBudget", 2, "heap ${'$'}{kb}KB > ${'$'}{kbLimit}KB")) else emptyList()
    }
}
