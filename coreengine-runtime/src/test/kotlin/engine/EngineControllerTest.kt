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

package engine

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.coreengine.runtime.engine.EngineController
import org.coreengine.runtime.engine.CoreEngine
import testutil.*

import org.coreengine.api.scene.Scene
import org.coreengine.api.scene.SceneManifest
import org.coreengine.api.resource.EngineServices
import org.coreengine.api.input.InputEvent
import org.coreengine.api.render.Renderer

class EngineControllerTest {

    private class TestScene : Scene {
        override val manifest = SceneManifest("demo")
        override val camera = FixedOrthoCamera()
        var updates = 0
        override fun onUpdate(dt: Float) {
            updates++
        }

        override fun onRender(renderer: Renderer) {}
        override fun onInput(event: InputEvent) = false
        override fun onCreateResources(services: EngineServices) {}
    }

    @Test
    fun startInvokesLoopAndSamples() {
        val scene = TestScene()
        val renderer = TestRenderer()

        val engine = CoreEngine.builder().apply {
            sceneFactory = { scene }          // <- en vez de .scene { … }
            this.renderer = renderer          // <- en vez de .renderer(...)
        }.build()

        val ticker = FakeTicker()
        val controller = EngineController(engine, ticker)
        var samples = 0
        controller.onSample = { samples++ }

        controller.start()
        // supera 1s con margen
        repeat(61) { ticker.tick(1f / 60f) }

        // verifica progreso real sin depender de begin/end
        assertTrue(scene.updates > 0)
        // 'clear' sí se llama en tu tickFrame
        assertTrue(renderer.clears > 0)
        // al menos una muestra
        assertTrue(samples >= 1)

        controller.stop()
        assertFalse(controller.isRunning)
    }
}

