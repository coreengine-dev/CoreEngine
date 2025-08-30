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
import org.coreengine.runtime.engine.SceneStack
import org.coreengine.api.scene.Scene
import org.coreengine.api.scene.SceneManifest
import org.coreengine.api.camera.Camera
import org.coreengine.api.entity.Entity
import org.coreengine.api.resource.EngineServices
import org.coreengine.api.input.InputEvent
import org.coreengine.api.render.Renderer
import org.coreengine.api.resource.ResourceManager
import org.coreengine.api.util.LogLevel
import org.coreengine.api.util.Logger


class SceneStackTest {

    private object NoopServices : EngineServices {
        override val renderer: Renderer = object : Renderer {
            override val drawCallsThisFrame: Int = 0
            override fun begin(camera: Camera) {}
            override fun draw(entity: Entity) {}
            override fun clear(r: Float, g: Float, b: Float, a: Float) {}
            override fun end() {}
        }

        override val resourceManager: ResourceManager = ResourceManager()

        override val logger: Logger = object : Logger {
            override var level: LogLevel = LogLevel.INFO
            override var tag: String = "Noop"
            override fun log(level: LogLevel, message: String, t: Throwable?, tag: String?) {}
        }
    }




    @Test
    fun setReplacePushPopLifecycle() {
        val stack = SceneStack().apply { setServices(NoopServices) }

        val s1 = TestScene("A")
        val s2 = TestScene("B")

        stack.set({ s1 })
        assertTrue(s1.resources); assertTrue(s1.created)
        assertEquals("A", stack.current?.id)

        stack.push({ s2 })
        assertEquals("B", stack.current?.id)
        assertTrue(s2.created)
        assertEquals(1, stack.backstackSize)

        assertTrue(stack.pop())
        assertEquals("A", stack.current?.id)
        assertTrue(s2.destroyed)
        assertEquals(0, stack.backstackSize)

        val s3 = TestScene("C")
        stack.replace({ s3 })
        assertEquals("C", stack.current?.id)
        assertTrue(s1.destroyed)
    }

    // ---------- helpers ----------

//    private object NoopServices : EngineServices

    private class TestScene(id: String) : Scene {
        override val manifest = SceneManifest(id)
        override val camera: Camera = FixedOrthoCamera()
        var resources = false
        var created = false
        var destroyed = false
        override fun onCreateResources(services: EngineServices) { resources = true }
        override fun onCreate() { created = true }
        override fun onDestroy() { destroyed = true }
        override fun onRender(renderer: Renderer) {}
        override fun onInput(event: InputEvent) = false
    }

    private class FixedOrthoCamera : Camera {
        override var rotation = 0f
        private var w = 0f; private var h = 0f
        override val width get() = w
        override val height get() = h
        override val xMin get() = 0f
        override val yMin get() = 0f
        override fun setSize(w: Float, h: Float) { this.w = w; this.h = h }
        override fun setViewport(x: Int, y: Int, w: Int, h: Int) {}
        override fun worldToScreen(x: Float, y: Float) = x to y
        override fun screenToWorld(x: Float, y: Float) = x to y
    }
}
