package org.coreengine.camera

/**
 * Cámara ortográfica.
 * - Origen configurable: TOP_LEFT (Android) o BOTTOM_LEFT (GL clásico).
 * - project/unproject: mundo ↔ pantalla con zoom y viewport.
 */
enum class Origin { TOP_LEFT, BOTTOM_LEFT }

open class Camera(
    var width: Float = 0f,
    var height: Float = 0f,
    var zoom: Float = 1f,
    var origin: Origin = Origin.TOP_LEFT
) {
    var viewportX: Int = 0
    var viewportY: Int = 0
    var viewportW: Int = 1
    var viewportH: Int = 1

    fun setSize(pWidth: Float, pHeight: Float) { width = pWidth; height = pHeight }
    fun setViewport(x: Int, y: Int, pWidth: Int, pHeight: Int) {
        viewportX = x; viewportY = y; viewportW = pWidth.coerceAtLeast(1); viewportH = pHeight.coerceAtLeast(1)
    }

    /** Pantalla(px) → Mundo(unidades) */
    fun unproject(sx: Float, sy: Float): Pair<Float, Float> {
        // normalizados a [0,1] dentro del viewport
        val nx = (sx - viewportX) / viewportW.toFloat()
        val ny = (sy - viewportY) / viewportH.toFloat()

        // a coords cámara
        val wx = (nx * width) / zoom
        val wyScreen = (ny * height) / zoom

        // ajustar origen vertical
        val wy = when (origin) {
            Origin.TOP_LEFT    -> wyScreen
            Origin.BOTTOM_LEFT -> (height / zoom) - wyScreen
        }
        return wx to wy
    }

    /** Mundo(unidades) → Pantalla(px) */
    fun project(wx: Float, wy: Float): Pair<Float, Float> {
        // ajustar origen vertical
        val yWorld = when (origin) {
            Origin.TOP_LEFT    -> wy
            Origin.BOTTOM_LEFT -> (height / zoom) - wy
        }

        val sx = ((wx * zoom) / width) * viewportW + viewportX
        val sy = ((yWorld * zoom) / height) * viewportH + viewportY
        return sx to sy
    }

    fun lookAt(x: Float, y: Float) { /* placeholder: pan futuro */ }
}
