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
