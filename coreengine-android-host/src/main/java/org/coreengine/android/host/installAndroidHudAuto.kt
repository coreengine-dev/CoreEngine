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