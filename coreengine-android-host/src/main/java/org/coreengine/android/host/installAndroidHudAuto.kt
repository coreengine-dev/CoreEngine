package org.coreengine.android.host

import org.coreengine.hud.HudLayer
import org.coreengine.integration.HostLocator
import org.coreengine.scene.Scene

/**
 *  Auto-inyecta  un HudAndroidLayer cuando el host Android está activo y
 *  hay overlay disponible.
 * Debe llamarse desde CoreSurfaceHost.bind().
 */
internal fun installAndroidHudAuto() {
    Scene.Companion.hudSupplier = {
        val bridge = HostLocator.host as? AndroidHostBridge
        if (bridge == null) null else HudAndroidLayer(bridge.overlayLayer) as HudLayer
    }
}