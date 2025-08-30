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
// Archivo: org/coreengine/scene/SceneManifest.kt
// Rol TCD: 𝝮 (límites de resonancia/recursos)
// Mantiene nombres de Scene/SceneManager existentes. Es externo.
// ─────────────────────────────────────────────────
package org.coreengine.api.scene

data class SceneManifest(
    val id: String,
    val memBudgetKB: Int = 5120,
    val maxSprites: Int = 2000,
    val maxTextChars: Int = 8000,
    val targetMsPerFrame: Float = 16.7f
)


