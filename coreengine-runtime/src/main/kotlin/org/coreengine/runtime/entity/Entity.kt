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

package api.coreengine.runtime.entity

import org.coreengine.camera.Camera
import org.coreengine.input.Action
import org.coreengine.input.InputEvent
import org.coreengine.input.InputListener
import org.coreengine.render.Renderer

/**
 * Nodo de escena: transform, hijos y ciclo.
 * onUpdate(dt) → recursivo; onDraw(renderer,camera) → recursivo.
 * TCD: ∇ en update de cada nodo; 𝐌 en draw.
 */


abstract class Entity : InputListener {
    val children = mutableListOf<Entity>()

    var x = 0f
    var y = 0f
    var width = 0f
    var height = 0f

    var scaleX = 1f
    var scaleY = 1f
    var rotation = 0f
    var zIndex = 0

    var visible = true
    var enabled = true

    private var clickListener: (() -> Unit)? = null

    fun setBounds(px: Float, py: Float, w: Float, h: Float) {
        x = px; y = py; width = w; height = h
    }

    fun attachChild(e: Entity) { children += e }
    fun detachChild(e: Entity) { children -= e }

    open fun onUpdate(delta: Float) {
        if (!enabled) return
        children.forEach { it.onUpdate(delta) }
    }

    open fun onDraw(renderer: Renderer, camera: Camera) {
        if (!visible) return
        // drawSelf(renderer, camera) // opcional en derivados
        children.sortedBy { it.zIndex }.forEach {
            it.onDraw(renderer, camera)
        }
    }

    /** AABB simple en coords de mundo. */
    open fun hitTest(px: Float, py: Float): Boolean {
        if (!visible || !enabled) return false
        return px >= x && px <= x + width && py >= y && py <= y + height
    }

    fun setOnClickListener(listener: (() -> Unit)?) { clickListener = listener }

    override fun onInput(ev: InputEvent): Boolean {
        if (!enabled || !visible) return false

        // hijos primero por z DESC
        val kids = children.sortedByDescending { it.zIndex }
        for (c in kids) if (c.onInput(ev)) return true

        // luego self
        if (ev is InputEvent.Touch && ev.action == Action.UP && hitTest(ev.x, ev.y)) {
            clickListener?.invoke()
            return clickListener != null
        }
        return false
    }
}