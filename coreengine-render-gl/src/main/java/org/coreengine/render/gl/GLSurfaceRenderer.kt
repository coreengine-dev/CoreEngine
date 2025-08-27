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

package org.coreengine.render.gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import org.coreengine.camera.Camera
import org.coreengine.entity.Entity
import org.coreengine.render.Renderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Renderer basado en OpenGL ES 2.0.
 * Gestiona el ciclo de vida del contexto GL y el pipeline de renderizado.
 */
class GLSurfaceRenderer : Renderer, GLSurfaceView.Renderer {

    private var currentCamera: Camera? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Config inicial de OpenGL
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Ajusta el viewport al tamaño actual
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        // Limpia pantalla
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        // Aquí se llamará a la lógica de dibujado de cada frame
        // TODO: iterar entidades y llamar draw()
    }

    override fun begin(camera: Camera) {
        currentCamera = camera
        // TODO: Configurar matrices de proyección y vista según la cámara
    }

    override fun draw(entity: Entity) {
        // TODO: Dibujar entidad con shaders y buffers
    }

    override fun clear(r: Float, g: Float, b: Float, a: Float) {
        GLES20.glClearColor(r, g, b, a)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    }


    // --- métrica ---
    private var draws = 0
    override val drawCallsThisFrame: Int get() = draws
    private inline fun markDraw(block: () -> Unit) { draws++; block() }

    override fun end() {
        // Nada específico aquí, el swap lo maneja GLSurfaceView automáticamente
        currentCamera = null
    }
}