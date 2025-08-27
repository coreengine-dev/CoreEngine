
// ─────────────────────────────────────────────────
// Archivo: org/coreengine/scene/SceneManifest.kt
// Rol TCD: 𝝮 (límites de resonancia/recursos)
// Mantiene nombres de Scene/SceneManager existentes. Es externo.
// ─────────────────────────────────────────────────
package org.coreengine.scene

data class SceneManifest(
    val id: String,
    val memBudgetKB: Int = 5120,
    val maxSprites: Int = 2000,
    val maxTextChars: Int = 8000,
    val targetMsPerFrame: Float = 16.7f
)
