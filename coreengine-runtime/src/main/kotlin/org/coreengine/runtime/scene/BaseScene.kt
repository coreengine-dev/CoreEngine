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

package org.coreengine.runtime.scene

import org.coreengine.api.camera.Camera
import org.coreengine.api.entity.Entity
import org.coreengine.api.hud.HudLayer
import org.coreengine.api.input.InputEvent
import org.coreengine.api.render.Renderer
import org.coreengine.api.resource.EngineServices
import org.coreengine.api.scene.Scene
import org.coreengine.api.scene.SceneManifest
import org.coreengine.runtime.util.Debug

/**
 * Escena base Phase 1.
 * - Mantiene entidades y HUDs por separado.
 * - Ordena por zIndex solo cuando cambia algo.
 * - Iteración con snapshots para evitar ConcurrentModification.
 * - HUD externo opcional vía Scene.hudSupplier.
 */
open class BaseScene(
    override val manifest: SceneManifest,
    override val camera: Camera
) : Scene {

    private val children = mutableListOf<Entity>()
    private val huds = mutableListOf<HudLayer>()

    private val drawChildren = mutableListOf<Entity>()
    private val drawHuds = mutableListOf<HudLayer>()
    private var orderDirty = true

    // ----- lifecycle -----
    override fun onCreateResources(services: EngineServices) {
        Debug.i("Loading resources for $id")
        Scene.hudSupplier?.invoke()?.let { attachHud(it) }
        onCreateResourcesInternal(services)
    }
    protected open fun onCreateResourcesInternal(services: EngineServices) {}

    override fun onCreate() {
        Debug.i("Scene $id created")
        // Solo entidades (HudLayer no define onCreate)
        children.forEach { it.onCreate() }
    }

    override fun onDestroy() {
        children.clear()
        huds.clear()
        drawChildren.clear()
        drawHuds.clear()
        orderDirty = true
        Debug.i("Scene $id destroyed")
    }

    // ----- frame loop -----
    override fun onUpdate(dt: Float) {
        ensureOrder()
        onPreUpdate(dt)

        val cs = drawChildren.toList()
        val hs = drawHuds.toList()

        cs.forEach { if (it.visible) it.onUpdate(dt) }
        hs.forEach { if (it.visible) it.onUpdate(dt) }

        onPostUpdate(dt)
    }
    protected open fun onPreUpdate(dt: Float) {}
    protected open fun onPostUpdate(dt: Float) {}

    override fun onRender(renderer: Renderer) {
        ensureOrder()
        drawChildren.forEach { if (it.visible) it.onDraw(renderer) }
        drawHuds.forEach { if (it.visible) it.onDraw(renderer) }
    }

    override fun onInput(event: InputEvent): Boolean {
        ensureOrder()
        // HUDs primero (top-most)
        for (h in drawHuds.asReversed()) if (h.visible && h.onInput(event)) return true
        for (e in drawChildren.asReversed()) if (e.visible && e.onInput(event)) return true
        return false
    }

    // ----- mutation API -----
    fun attachChild(e: Entity) {
        children += e; orderDirty = true
    }
    fun detachChild(e: Entity) {
        children -= e; orderDirty = true
    }

    fun attachHud(h: HudLayer) {
        huds += h; orderDirty = true
    }
    fun detachHud(h: HudLayer) {
        huds -= h; orderDirty = true
    }

    fun clearChildren() { children.clear(); orderDirty = true }
    fun clearHuds() { huds.clear(); orderDirty = true }

    // Read-only views si las necesitas externamente
    val entities: List<Entity> get() = drawChildren
    val hudLayers: List<HudLayer> get() = drawHuds

    // ----- ordering -----
    private fun ensureOrder() {
        if (!orderDirty) return
        drawChildren.clear()
        drawChildren.addAll(children.sortedBy { it.zIndex })
        drawHuds.clear()
        drawHuds.addAll(huds.sortedBy { it.zIndex })
        orderDirty = false
    }
}
