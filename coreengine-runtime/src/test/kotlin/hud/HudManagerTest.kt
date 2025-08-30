package hud

import org.coreengine.api.camera.Camera
import org.coreengine.api.entity.Entity
import org.coreengine.api.hud.HudLayer
import org.coreengine.api.render.Renderer
import org.coreengine.runtime.engine.HudManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HudManagerTest {

    private class FixedOrthoCamera : Camera {
        override var rotation = 0f
        override val width = 100f
        override val height = 100f
        override val xMin = 0f
        override val yMin = 0f
        override fun setSize(w: Float, h: Float) {}
        override fun setViewport(x: Int, y: Int, w: Int, h: Int) {}
        override fun worldToScreen(x: Float, y: Float) = x to y
        override fun screenToWorld(x: Float, y: Float) = x to y
    }

    private class TestRenderer : Renderer {
        var draws = 0
        override val drawCallsThisFrame get() = draws
        override fun begin(camera: Camera) {}
        override fun draw(entity: Entity) {
            draws++
        }

        override fun clear(r: Float, g: Float, b: Float, a: Float) {}
        override fun end() {}
    }

    private class CountingHud(override var zIndex: Int) : HudLayer {
        override var visible: Boolean = true
        var renders = 0
        override fun onDraw(renderer: Renderer) {
            renders++
        }
    }

    @Test
    fun addIsIdempotentAndDrawCallsOnce() {
        val hm = HudManager()
        val hud = CountingHud(5)
        val cam = FixedOrthoCamera()
        val r = TestRenderer()

        hm.addLayerIfAbsent(hud)
        hm.addLayerIfAbsent(hud) // idempotente

        hm.draw(r, cam)
        assertEquals(1, hud.renders) // solo una vez aunque se añadió dos veces

        hm.removeLayer(hud)
        // dibujar sin capas no debe fallar ni aumentar renders
        hm.draw(r, cam)
        assertEquals(1, hud.renders)
    }
}
