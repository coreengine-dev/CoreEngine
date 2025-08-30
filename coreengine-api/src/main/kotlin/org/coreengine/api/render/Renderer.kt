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

package org.coreengine.api.render

import org.coreengine.api.camera.Camera
import org.coreengine.api.entity.Entity


// Renderer — Responsable del renderizado (𝐌: Manifestación)
// - Dibuja entidades en pantalla con el backend elegido.
// - En TCD: corresponde a la fase de manifestación material.
/**
 * API de dibujo de alto nivel.
 * begin(camera) → draw(entity)* → end().
 * clear() para limpiar frame.
 * TCD: 𝐌 (manifestación material del frame).
 */

interface Renderer {
    fun begin(camera: Camera)
    fun draw(entity: Entity)
    fun end()
    fun clear(r: Float, g: Float, b: Float, a: Float)
    val drawCallsThisFrame: Int
}
