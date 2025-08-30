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

package input
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import testutil.FixedOrthoCamera
import org.coreengine.api.scene.Scene
import org.coreengine.api.scene.SceneManifest
import org.coreengine.api.input.InputEvent
import org.coreengine.api.render.Renderer
import org.coreengine.api.resource.EngineServices

class InputPipelineTest {
    private class TestScene : Scene {
        override val manifest = SceneManifest("X")
        override val camera = FixedOrthoCamera()
        var inputs=0
        override fun onInput(event: InputEvent) = (++inputs) > 0
        override fun onRender(renderer: Renderer) {}
        override fun onCreateResources(services: EngineServices) {}
    }
    @Test fun sceneReceivesInput() {
        val s = TestScene()
        val e = InputEvent.Down(10f, 20f, 0)
        assertTrue(s.onInput(e))
        assertEquals(1, s.inputs)
    }
}

