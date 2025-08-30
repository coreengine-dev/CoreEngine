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
// Archivo: org/coreengine/resource/font/DynamicTextMesh.kt
// Rol TCD: 𝐌 (representación) con disciplina 𝝮 (capacidad controlada)
// Sustituye SOLO el backend interno de tu Text para evitar OOB.
// Mantén tu API pública de Text.
// ─────────────────────────────────────────────────
package org.coreengine.runtime.font

class DynamicTextMesh(initialCap: Int = 128) {
    private var cap = if (initialCap < 16) 16 else initialCap
    fun ensure(n: Int) { if (n > cap) { while (cap < n) cap = cap shl 1; /* recrea VBOs con cap */ } }
    fun setText(s: String) { ensure(s.length); /* re-shape + update VBO */ }
    fun capacity(): Int = cap
}
