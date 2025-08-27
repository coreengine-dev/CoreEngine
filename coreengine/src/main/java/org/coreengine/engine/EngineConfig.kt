package org.coreengine.engine

import org.coreengine.render.RenderBackend


/**
 * Configuración del motor.
 * TCD: 𝛂 (tiempo base) influye en targetFps/vsync.
 */
data class EngineConfig(
    val targetFps: Int = 60,
    val useVSync: Boolean = true,
    val renderBackend: RenderBackend = RenderBackend.CANVAS
)