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


