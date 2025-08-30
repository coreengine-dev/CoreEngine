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
import org.coreengine.api.entity.Entity
import org.coreengine.api.render.Renderer

class TestRenderer : Renderer {
    var beginCount=0; var endCount=0; var clears=0; private var draws=0
    override val drawCallsThisFrame get() = draws
    override fun begin(camera: Camera) { beginCount++; draws=0 }
    override fun draw(entity: Entity) { draws++ }
    override fun clear(r: Float, g: Float, b: Float, a: Float) { clears++ }
    override fun end() { endCount++ }
}
