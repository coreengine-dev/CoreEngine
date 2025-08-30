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

// ─────────────────────────────────────────────────
// Archivo: org/coreengine/framecheck/FrameChecker.kt
// Rol TCD: 𝝮→𝝎 (observadores modulables tipo "checkers")
// ─────────────────────────────────────────────────
package api.coreengine.runtime.engine.metrics

import org.coreengine.state.EngineSnapshot

data class CheckerReport(val kind: String, val severity: Int, val details: String)

interface FrameChecker { fun onFrame(s: EngineSnapshot): List<CheckerReport> }

class MemBudgetChecker(private val kbLimit: Int): FrameChecker {
    override fun onFrame(s: EngineSnapshot): List<CheckerReport> {
        val kb = s.counters["heapKb"] ?: return emptyList()
        return if (kb > kbLimit) listOf(CheckerReport("MemBudget", 2, "heap ${'$'}{kb}KB > ${'$'}{kbLimit}KB")) else emptyList()
    }
}
