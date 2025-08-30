package scene

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.coreengine.api.camera.Camera
import org.coreengine.api.entity.Entity
import org.coreengine.api.hud.HudLayer
import org.coreengine.api.input.InputEvent
import org.coreengine.api.render.Renderer
import org.coreengine.api.resource.EngineServices
import org.coreengine.api.resource.ResourceManager
import org.coreengine.api.scene.SceneManifest
import org.coreengine.api.util.LogLevel
import org.coreengine.api.util.Logger
import org.coreengine.runtime.scene.BaseScene

class BaseSceneTest {

    // ---- dummies ----
    private class FixedOrthoCamera : Camera {
        override var rotation = 0f
        private var w = 100f;
        private var h = 100f
        override val width get() = w
        override val height get() = h
        override val xMin get() = 0f
        override val yMin get() = 0f
        override fun setSize(w: Float, h: Float) {
            this.w = w; this.h = h
        }

        override fun setViewport(x: Int, y: Int, w: Int, h: Int) {}
        override fun worldToScreen(x: Float, y: Float) = x to y
        override fun screenToWorld(x: Float, y: Float) = x to y
    }

    private class TestRenderer : Renderer {
        var clears = 0;
        var draws = 0
        override val drawCallsThisFrame get() = draws
        override fun begin(camera: Camera) {}
        override fun draw(entity: Entity) {
            draws++
        }

        override fun clear(r: Float, g: Float, b: Float, a: Float) {
            clears++
        }

        override fun end() {}
    }

    private class DummyEntity(
        override var zIndex: Int,
        var consumeInput: Boolean = false
    ) : Entity {
        override var visible: Boolean = true
        var updates = 0;
        var renders = 0;
        var inputs = 0
        override fun onUpdate(dt: Float) {
            updates++
        }

        override fun onDraw(renderer: Renderer) {
            renders++
        }

        override fun onInput(event: InputEvent): Boolean {
            inputs++; return consumeInput
        }
    }

    private class DummyHud(
        override var zIndex: Int,
        var consumeInput: Boolean = false
    ) : HudLayer {
        override var visible: Boolean = true
        var updates = 0;
        var renders = 0;
        var inputs = 0
        override fun onUpdate(dt: Float) {
            updates++
        }

        override fun onDraw(renderer: Renderer) {
            renders++
        }

        override fun onInput(ev: InputEvent): Boolean {
            inputs++; return consumeInput
        }
    }

    private val services = object : EngineServices {
        override val renderer: Renderer = TestRenderer()
        override val resourceManager: ResourceManager = ResourceManager()
        override val logger = object : Logger {
            override var level = LogLevel.INFO
            override var tag: String = "test"
            override fun log(
                level: LogLevel,
                message: String,
                t: Throwable?,
                tag: String?
            ) {

            }

        }
    }

    // ---- tests ----
    @Test
    fun zOrderingAndVisibility() {
        val scene = object : BaseScene(SceneManifest("s"), FixedOrthoCamera()) {}
        val eLow = DummyEntity(zIndex = 0)
        val eHigh = DummyEntity(zIndex = 10)
        val hMid = DummyHud(zIndex = 5)

        scene.onCreateResources(services)
        scene.attachChild(eHigh)
        scene.attachChild(eLow)
        scene.attachHud(hMid)

        // update + render
        scene.onUpdate(0.016f)
        scene.onRender(TestRenderer())

        // todos actualizan y dibujan
        assertEquals(1, eLow.updates)
        assertEquals(1, eHigh.updates)
        assertEquals(1, hMid.updates)
        assertEquals(1, eLow.renders)
        assertEquals(1, eHigh.renders)
        assertEquals(1, hMid.renders)

        // visibilidad afecta
        eHigh.visible = false
        hMid.visible = false
        scene.onUpdate(0.016f)
        scene.onRender(TestRenderer())
        assertEquals(1, eHigh.renders) // no aumentó
        assertEquals(1, hMid.renders)
    }

    @Test
    fun inputRoutingHudFirst() {
        val scene = object : BaseScene(SceneManifest("s"), FixedOrthoCamera()) {}
        val entity = DummyEntity(zIndex = 0, consumeInput = true)
        val hud = DummyHud(zIndex = 10, consumeInput = true)

        scene.onCreateResources(services)
        scene.attachChild(entity)
        scene.attachHud(hud)

        // HUD debe consumir primero
        val consumed = scene.onInput(InputEvent.Down(1f, 1f, 0))
        assertTrue(consumed)
        assertEquals(1, hud.inputs)
        // entity no debería recibir si HUD ya consumió
        assertEquals(0, entity.inputs)
    }
}
