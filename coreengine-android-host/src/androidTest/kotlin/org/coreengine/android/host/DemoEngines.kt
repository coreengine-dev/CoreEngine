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

package org.coreengine.android.host

import org.coreengine.api.camera.Camera
import org.coreengine.api.scene.Scene
import org.coreengine.api.scene.SceneManifest
import org.coreengine.canvas.CanvasRenderer
import org.coreengine.runtime.camera.FixedOrthoCamera
import org.coreengine.runtime.engine.CoreEngine

object DemoEngines {
    fun makeMinimalEngine(): CoreEngine =
        CoreEngine.builder().apply {
            renderer = CanvasRenderer()
            sceneFactory = { TestScene() }   // <-- aquí
        }.build()
}

class TestScene : Scene {
    override val manifest = SceneManifest("demo")
    override val camera: Camera = FixedOrthoCamera()
}




