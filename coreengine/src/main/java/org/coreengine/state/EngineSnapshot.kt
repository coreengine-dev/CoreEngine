// ─────────────────────────────────────────────────
// Archivo: org/coreengine/state/EngineSnapshot.kt
// Rol TCD: 𝝎 (foto inmutable del tick para diagnóstico)
// No interfiere con tus tipos StructureState: conviven.
// ─────────────────────────────────────────────────
package org.coreengine.state

/**
 * Snapshot ligero del estado visible del motor.
 * (Métricas por frame; sin serializar escenas todavía).
 */
data class EngineSnapshot(
    val frame: Long,
    val sceneId: String,
    val counters: Map<String, Int>,
    val timersMs: Map<String, Float>,
)
