package org.coreengine.android.host

import android.content.Context
import android.widget.FrameLayout
import android.view.View
import org.coreengine.integration.HostLocator
import org.coreengine.scene.Scene

/**
 * Extensiones de conveniencia para usar funciones Android desde Scene
 * sin acoplar el core a android.*. Requiere que el host haya hecho bind()
 * y registrado AndroidHostBridge en HostLocator.host.
 */

/** Contexto Android actual. Falla temprano si no hay host. */
val Scene.context: Context
    get() = (HostLocator.host as? AndroidHostBridge)?.activity
        ?: error("Host Android no adjunto. Llama CoreSurfaceHost.bind(activity, engine) antes de usar Scene.context.")

/** Ejecuta en el hilo UI del host. No bloquear. */
fun Scene.ui(block: () -> Unit) {
    (HostLocator.host as? AndroidHostBridge)?.postToUi(block)
        ?: error("Host Android no adjunto. CoreSurfaceHost.bind(...) requerido.")
}

/**
 * Añade una View Android al overlay sobre la Surface.
 * Idempotente si reusas el mismo id.
 */
fun Scene.addAndroidView(
    build: (Context) -> View,
    lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    ),
    id: String = "view-${System.nanoTime()}",
) {
    val bridge = (HostLocator.host as? AndroidHostBridge) ?: return
    bridge.overlayAdd(id) { act -> build(act) to lp }
}

/** Elimina una View del overlay por id. No falla si no existe. */
fun Scene.removeAndroidView(id: String) {
    (HostLocator.host as? AndroidHostBridge)?.overlayRemove(id)
}

/** Limpia todas las Views del overlay. Úsalo en onDestroy(). */
fun Scene.clearAndroidViews() {
    (HostLocator.host as? AndroidHostBridge)?.overlayClear()
}


