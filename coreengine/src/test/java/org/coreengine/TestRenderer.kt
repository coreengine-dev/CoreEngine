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

// src/test/java/org/coreengine/engine/EngineLoopTest.kt

import org.coreengine.camera.Camera
import org.coreengine.engine.CoreEngine
import org.coreengine.engine.EngineConfig
import org.coreengine.engine.EngineController
import org.coreengine.engine.SceneFactory
import org.coreengine.entity.Entity
import org.coreengine.render.NoopRenderer
import org.coreengine.render.Renderer
import org.coreengine.scene.Scene
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.DefaultAsserter.assertTrue

// Renderer de prueba: registra begin/clear/draw/end por frame
private class TestRenderer : Renderer {
    val begins = AtomicInteger(0)
    val ends = AtomicInteger(0)
    val clears = AtomicInteger(0)
    override fun begin(camera: Camera) { begins.incrementAndGet() }
    override fun draw(entity: Entity) {}
    override fun clear(r: Float, g: Float, b: Float, a: Float) { clears.incrementAndGet() }
    override val drawCallsThisFrame: Int
        get() = 0

    override fun end() { ends.incrementAndGet() }
}

// Escena que cuenta updates y pide parar en N frames
private class CountingScene(
    private val stopAfter: Int,
    private val onStop: () -> Unit
) : Scene() {
    val updates = AtomicInteger(0)
    override fun onUpdate(delta: Float) {
        val u = updates.incrementAndGet()
        if (u >= stopAfter) onStop()
    }
}

class EngineLoopTest {

    // EngineLoopTest.kt
    @Test
    fun engine_runs_update_then_render_and_stops_on_request() {
        val renderer = NoopRenderer

        val engine = CoreEngine.Builder().apply {
            config = EngineConfig(targetFps = 120, useVSync = false)  // sin FPS HUD (no pongas fpsHudFactory)
            this.renderer = renderer
            // NO setees sceneFactory aquí
        }.build()

        val stopped = CountDownLatch(1)
        val controller = EngineController(engine)

        val scene = object : Scene() {
            val updates = AtomicInteger(0)
            override fun onUpdate(delta: Float) {
                if (updates.incrementAndGet() >= 3) {
                    engine.stop()
                    stopped.countDown()
                }
            }
        }

        // Asegura escena antes de start
        engine.sceneManager.setScene(scene)

        controller.start()
        val finished = stopped.await(3, TimeUnit.SECONDS)   // 3s por holgura
        controller.stop()

        assertTrue("loop no paró a tiempo", finished)
    }


    // RendererContractTest.kt
    @Test fun renderer_end_always_called() {
        val spy = SpyRenderer()
        val fired = CountDownLatch(1)

        val engine = CoreEngine.Builder().apply {
            renderer = spy
            sceneFactory = SceneFactory { BoomScene(fired) }
            config = EngineConfig(targetFps = 120, useVSync = false)
        }.build()

        val ctl = EngineController(engine)
        ctl.start()

        assertTrue("boom no ocurrió", fired.await(1, TimeUnit.SECONDS))
        ctl.stop() // asegura finally{ end() }

        assertTrue(spy.calls.contains("begin"))
        assertTrue(spy.calls.contains("end"))
    }

    @Test
    fun draw_order_respects_zindex() {
        val r = TraceRenderer()
        val s = ZScene()
        r.begin(s.camera); s.onDraw(r, s.camera); r.end()
        assertEquals("ACB", r.trace.toString())

    }

}
