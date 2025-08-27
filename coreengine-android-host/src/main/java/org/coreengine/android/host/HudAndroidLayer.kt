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

package org.coreengine.android.host


import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import org.coreengine.hud.HudLayer

/**
 * HudAndroidLayer — HUD basado en Views de Android.
 *
 * - Encima de la SurfaceView, en el overlay del CoreSurfaceHost.
 * - Permite añadir y quitar Views (ej: Button, TextView).
 * - Los toques se manejan primero por estos Views, y si no se consumen
 *   bajan a la SurfaceView → Entities → Escena.
 */

// HudAndroidLayer.kt
class HudAndroidLayer(internal val overlay: FrameLayout) : HudLayer() {
    fun addView(v: View, lp: ViewGroup.LayoutParams) = overlay.addView(v, lp)
    fun removeView(v: View) = overlay.removeView(v)
    fun clear() = overlay.removeAllViews()
}


class HudAndroidLayer2(val overlay: FrameLayout) : HudLayer() {

    /** Añadir View al overlay con LayoutParams opcionales */
    fun addView(
        v: View,
        lp: ViewGroup.LayoutParams = defaultParams()
    ) {
        overlay.addView(v, lp)
    }

    /** Eliminar View del overlay */
    fun removeView(v: View) {
        overlay.removeView(v)
    }

    /** Eliminar todos los Views del overlay */
    fun clear() {
        overlay.removeAllViews()
    }

    /** LayoutParams por defecto: WRAP_CONTENT arriba a la izquierda */
    private fun defaultParams(): ViewGroup.LayoutParams =
        FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
}


/** HUD Android real: agrega Views al overlay. */
class HudAndroidLayer1(private val overlay: FrameLayout) : HudLayer() {
    fun addView(
        v: View,
        lp: ViewGroup.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    ) = overlay.addView(v, lp)

    fun removeView(v: View) = overlay.removeView(v)
    fun clear() = overlay.removeAllViews()
}

