// ─────────────────────────────────────────────────
// Archivo: org/coreengine/resource/font/DynamicTextMesh.kt
// Rol TCD: 𝐌 (representación) con disciplina 𝝮 (capacidad controlada)
// Sustituye SOLO el backend interno de tu Text para evitar OOB.
// Mantén tu API pública de Text.
// ─────────────────────────────────────────────────
package org.coreengine.resource.font

class DynamicTextMesh(initialCap: Int = 128) {
    private var cap = if (initialCap < 16) 16 else initialCap
    fun ensure(n: Int) { if (n > cap) { while (cap < n) cap = cap shl 1; /* recrea VBOs con cap */ } }
    fun setText(s: String) { ensure(s.length); /* re-shape + update VBO */ }
    fun capacity(): Int = cap
}
