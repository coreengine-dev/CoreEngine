package org.coreengine.render

import org.coreengine.camera.Camera
import org.coreengine.entity.Entity
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