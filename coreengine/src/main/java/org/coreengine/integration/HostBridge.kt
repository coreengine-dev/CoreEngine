// integration/HostBridge.kt  (core, sin android.*)
package org.coreengine.integration

interface HostBridge {
    val overlay: UiOverlay

    // ejecuta en hilo UI del host
    fun postToUi(block: () -> Unit)
}
