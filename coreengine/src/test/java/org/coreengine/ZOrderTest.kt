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

package org.coreengine

// ZOrderTest.kt

import org.coreengine.camera.Camera
import org.coreengine.entity.Entity
import org.coreengine.render.Renderer
import org.coreengine.scene.Scene
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TraceRenderer : Renderer {
    val trace = StringBuilder()
    override fun begin(camera: Camera) {}
    override fun clear(r: Float, g: Float, b: Float, a: Float) {}
    override val drawCallsThisFrame: Int
        get() = 0

    override fun draw(entity: Entity) {}
    override fun end() {}
}

private class TraceEntity(private val id: String, z: Int) : Entity() {
    init { zIndex = z }
    override fun onDraw(r: Renderer, c: Camera) {
        (r as? TraceRenderer)?.trace?.append(id)
    }
}

class ZScene : Scene() {
    private val a: TraceEntity = TraceEntity("A", 0)
    private val b: TraceEntity = TraceEntity("B", 2)
    private val cNode: TraceEntity = TraceEntity("C", 1)

    override fun onDraw(r: Renderer, cam: Camera) {   // ← renombrado
        val nodes: List<TraceEntity> = listOf(a, b, cNode)
        nodes.sortedBy { it.zIndex }
            .forEach { it.onDraw(r, cam) }
    }
}


class ZOrderTest {
    @Test
    fun draw_order_respects_zindex() {
        val r = TraceRenderer()
        val s = ZScene()
        r.begin(s.camera)
        s.onDraw(r, s.camera)
        r.end()
        assertEquals("ACB", r.trace.toString())
    }
}


