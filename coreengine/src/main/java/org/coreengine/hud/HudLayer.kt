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



