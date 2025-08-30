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

package org.coreengine.render.canvas.hud

import org.coreengine.camera.Camera
import org.coreengine.input.InputEvent
import org.coreengine.render.Renderer

/**
 * HudManager — Gestiona HUDs superpuestos a la escena.
 *
 * - Cada HUD es una "capa perceptual" independiente.
 * - Se dibuja SIEMPRE encima de la escena activa.
 * - Recibe input antes que la escena (prioridad HUD).
 *
 * TCD: capa de retroalimentación perceptual → 𝐌 visible,
 *      pero intercepta 𝚿 (input) antes de ∇ (update).
 */
class HudManager {

    private val layers: MutableList<HudLayer> = mutableListOf()

    /** Añadir una capa HUD (ej: UI, overlay debug). */
    fun addLayer(layer: HudLayer) {
        layers.add(layer)
    }

    /** Eliminar capa HUD. */
    fun removeLayer(layer: HudLayer) {
        layers.remove(layer)
    }

    /** Dibujar todas las capas HUD (encima de la escena). */
    fun draw(renderer: Renderer, camera: Camera) {
        layers
            .filter { it.visible }
            .sortedBy { it.zIndex }
            .forEach { it.onDraw(renderer, camera) }
    }


    /** Pasar input a las capas (prioridad sobre escena). */
    fun onInput(ev: InputEvent): Boolean {
        for (layer in layers.reversed()) { // el último HUD agregado está "encima"
            if (layer.onInput(ev)) return true
        }
        return false
    }

    fun clear() { layers.clear() }
    fun addLayerIfAbsent(layer: HudLayer) {
        if (layers.none { it === layer }) layers.add(layer)
    }

}


