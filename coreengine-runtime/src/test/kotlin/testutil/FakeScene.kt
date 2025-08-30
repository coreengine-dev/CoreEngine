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

package testutil
import org.coreengine.api.camera.Camera
import org.coreengine.api.hud.HudLayer
import org.coreengine.api.input.InputEvent
import org.coreengine.api.render.Renderer
import org.coreengine.api.resource.EngineServices
import org.coreengine.api.scene.Scene
import org.coreengine.api.scene.SceneManifest

class FakeScene(
    override val manifest: SceneManifest,
    override val camera: Camera
) : Scene {
    var created=false; var destroyed=false; var resources=false
    var inputs=0; var updates=0; var renders=0
    override fun onCreateResources(services: EngineServices) { resources=true }
    override fun onCreate() { created=true }
    override fun onDestroy() { destroyed=true }
    override fun onUpdate(dt: Float) { updates++ }
    override fun onRender(renderer: Renderer) { renders++ }
    override fun onInput(event: InputEvent): Boolean { inputs++; return false }
}

