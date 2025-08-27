// :coreengine
package org.coreengine.resource

import org.coreengine.engine.CoreEngine
import org.coreengine.render.Renderer

interface VbomProvider { fun create(capacity: Int): VertexBuffer }

data class EngineServices(
    val engine: CoreEngine,
    val vbom: VbomProvider,
    val fontManager: FontManager,
    val textureManager: TextureManager,
    val renderer: Renderer = engine.renderer,
)
