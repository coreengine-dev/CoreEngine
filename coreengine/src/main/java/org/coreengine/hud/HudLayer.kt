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

package org.coreengine.hud

import org.coreengine.entity.Entity
import org.coreengine.input.InputEvent
import org.coreengine.input.InputListener

/**
 * Capa HUD dibujada sobre la escena.
 * Ideal para UI/overlays/depuración.
 */

/**
 * Capa HUD — igual que Scene pero sin lógica de mundo.
 * Pensada para botones, overlays, debug panels.
 */
// org.coreengine.hud.HudLayer
open class HudLayer : Entity(), InputListener {
    override fun onInput(ev: InputEvent): Boolean {
        // igual que Entity: prioriza hijos por z
        val sorted = children.sortedByDescending { it.zIndex }
        for (c in sorted) if (c.onInput(ev)) return true
        // si el HUD mismo fuese clickeable:
        return super.onInput(ev)
    }
}



