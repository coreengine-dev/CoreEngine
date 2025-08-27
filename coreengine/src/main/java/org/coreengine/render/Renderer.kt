package org.coreengine.render

import org.coreengine.camera.Camera
import org.coreengine.entity.Entity


// Renderer — Responsable del renderizado (𝐌: Manifestación)
// - Dibuja entidades en pantalla con el backend elegido.
// - En TCD: corresponde a la fase de manifestación material.
/**
 * API de dibujo de alto nivel.
 * begin(camera) → draw(entity)* → end().
 * clear() para limpiar frame.
 * TCD: 𝐌 (manifestación material del frame).
 */

interface Renderer {
    fun begin(camera: Camera)
    fun draw(entity: Entity)
    fun end()
    fun clear(r: Float, g: Float, b: Float, a: Float)
    val drawCallsThisFrame: Int
}


