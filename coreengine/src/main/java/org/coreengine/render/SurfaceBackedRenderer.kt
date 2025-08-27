package org.coreengine.render

/** Renderers que necesitan una superficie del host para poder dibujar. */
interface SurfaceBackedRenderer {
    val hasSurface: Boolean
}