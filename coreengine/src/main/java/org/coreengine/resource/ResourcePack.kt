package org.coreengine.resource

/**
 * Paquete de recursos agrupados por escena o app.
 * Preparado para scopes (Scene/App) y release automático.
 */

data class ResourcePack(
    val textures: Map<String, Any> = emptyMap(),
    val fonts: Map<String, Any> = emptyMap()
)