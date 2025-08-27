package org.coreengine.engine

import org.coreengine.scene.Scene

/**
 * Fábrica de escenas para navegación y diferir construcción.
 * TCD: evita acoplar creación con ejecución de ∇/𝐌.
 *  * Se añade 'id' por defecto (no abstracto) para identificar la escena.
 */

fun interface SceneFactory {
    fun create(): Scene
    val id: String get() = this::class.java.name
}