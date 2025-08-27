package org.coreengine.render

/**
 * Backend de render seleccionado por el motor.
 * GLS = OpenGL ES, CANVAS = Canvas Android.
 * TCD: 𝐌 (manifestación).
 */

enum class RenderBackend { GLS, CANVAS ,OPENGL}


// Host EGL para GL (define tu interfaz real)
interface GLSurfaceHost {
    fun makeCurrent()
    fun swapBuffers()
    val width: Int
    val height: Int
}



