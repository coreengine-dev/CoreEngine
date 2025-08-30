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

// android/AndroidHostBridge.kt
package org.coreengine.android.host

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import org.coreengine.integration.HostBridge
import org.coreengine.integration.UiOverlay

/**
 * Puente Android ↔ Core:
 * - Provee acceso al hilo UI (postToUi).
 * - Gestiona el overlay de Views Android por encima de la Surface.
 * - Implementa UiOverlay sin filtrar android.* al core.
 */
class AndroidHostBridge(
    /** Activity anfitriona. */
    val activity: Activity,
    /** Contenedor del overlay de UI sobre la Surface. */
    private val overlayContainer: FrameLayout
) : HostBridge {

    /** Exposición de solo lectura del overlay (p.ej. para HudAndroidLayer). */
    val overlayLayer: FrameLayout
        get() = overlayContainer

    /** Handler del hilo principal. */
    val main = Handler(Looper.getMainLooper())

    // Implementación de UiOverlay consumida por el core a través de HostBridge.overlay
    private val overlayImpl = object : UiOverlay {
        override fun add(id: String, build: () -> Any) {
            // build() devuelve Pair<View, LayoutParams> en nuestras extensiones
            main.post {
                val (v, lp) = build() as Pair<View, FrameLayout.LayoutParams>
                v.tag = id
                remove(id) // idempotente por id
                overlayContainer.addView(v, lp)
            }
        }
        override fun remove(id: String) {
            main.post {
                overlayContainer.children().firstOrNull { it.tag == id }?.let { overlayContainer.removeView(it) }
            }
        }
        override fun clear() {
            main.post { overlayContainer.removeAllViews() }
        }
    }

    /** Interfaz neutra que ve el core. */
    override val overlay: UiOverlay get() = overlayImpl

    /** Ejecuta en el hilo UI del host. */
    override fun postToUi(block: () -> Unit) { main.post(block) }

    // -------- Helpers para extensiones Android de Scene --------

    /** Añade View al overlay con id estable. */
    fun overlayAdd(
        id: String,
        build: (ctx: Activity) -> Pair<View, FrameLayout.LayoutParams>
    ) {
        overlay.add(id) { build(activity) }
    }

    /** Elimina por id si existe. */
    fun overlayRemove(id: String) { overlay.remove(id) }

    /** Limpia todas las Views del overlay. */
    fun overlayClear() { overlay.clear() }
}

/** Iterador simple de hijos del FrameLayout sin allocs extra. */
private fun FrameLayout.children(): Sequence<View> = sequence {
    for (i in 0 until childCount) yield(getChildAt(i))
}


