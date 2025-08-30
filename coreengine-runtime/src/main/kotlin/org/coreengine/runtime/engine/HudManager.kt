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

package org.coreengine.runtime.engine

import org.coreengine.api.camera.Camera
import org.coreengine.api.hud.HudLayer
import org.coreengine.api.render.Renderer

/**
 * Gestor de HUD en runtime.
 * - Mantiene la lista de capas HUD.
 * - Dibuja en orden de zIndex.
 * - Evita duplicados con addLayerIfAbsent.
 */
class HudManager {

    private val layers = mutableListOf<HudLayer>()

    fun addLayer(layer: HudLayer) {
        layers += layer
    }

    fun addLayerIfAbsent(layer: HudLayer) {
        if (layer !in layers) layers += layer
    }

    fun removeLayer(layer: HudLayer) {
        layers.remove(layer)
    }

    fun clear() {
        layers.clear()
    }

    /** Dibuja HUD por delante del mundo. */
    fun draw(renderer: Renderer, camera: Camera) {
        layers
            .asSequence()
            .filter { it.visible }
            .sortedBy { it.zIndex }
            .forEach { it.onDraw(renderer) }
    }

    /** Exposición opcional si necesitas consultar desde fuera. */
    fun provideHud(): List<HudLayer> = layers.toList()
}
