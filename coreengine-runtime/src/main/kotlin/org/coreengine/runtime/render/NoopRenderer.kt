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

package org.coreengine.runtime.render

import org.coreengine.api.camera.Camera
import org.coreengine.api.entity.Entity
import org.coreengine.api.render.Renderer


/**
 * Renderer nulo para tests/JUnit.
 * No toca GPU. Útil para validar ∇ sin 𝐌.
 */

object NoopRenderer : Renderer {
    override fun begin(camera: Camera) {}
    override fun draw(entity: Entity) {}
    override fun end() {}
    override fun clear(r: Float, g: Float, b: Float, a: Float) {}
    override val drawCallsThisFrame: Int = 0
}