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

package org.coreengine.demo

import org.coreengine.api.render.Renderer
import org.coreengine.api.scene.SceneManifest
import org.coreengine.runtime.camera.FixedOrthoCamera
import org.coreengine.runtime.scene.BaseScene
import org.coreengine.runtime.util.Debug

class SimpleScene : BaseScene(SceneManifest("demo"), FixedOrthoCamera()) {
    override fun onCreate() {
        Debug.i("create simpleScene")
    }

    override fun onUpdate(dt: Float) {
    }

    override fun onRender(renderer: Renderer) {
        renderer.clear(0f, 0.2f, 0f, 1f)
    }
}
