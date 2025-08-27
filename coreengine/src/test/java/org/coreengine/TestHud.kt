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

package org.coreengine

// src/test/java/org/coreengine/input/InputDispatchTest.kt

import org.coreengine.hud.HudLayer
import org.coreengine.hud.HudManager
import org.coreengine.input.Action
import org.coreengine.input.InputEvent
import org.coreengine.input.InputManager
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private class TestHud(private val hit: (Float,Float)->Boolean): HudLayer() {
    var consumed = false
    override fun onInput(ev: InputEvent): Boolean {
        if (ev is InputEvent.Touch && ev.action == Action.DOWN && hit(ev.x, ev.y)) {
            consumed = true
            return true
        }
        return super.onInput(ev)
    }
}



class InputDispatchTest {


    @Test
    fun hud_has_priority_over_scene_and_global() {
        val mgr = HudManager()
        val scene = TestScene()
        val input = InputManager()

        // HUD cubre 0..50 x 0..50 en coords de mundo
        val hud = TestHud { x, y -> x in 0f..50f && y in 0f..50f }
        mgr.addLayer(hud)

        var globalConsumed = false
        input.addGlobalListener { ev -> if (ev is InputEvent.Touch) globalConsumed = true }

        // Post en pantalla (que el Camera unproject convierte 1:1)
        input.post(InputEvent.Touch(25f, 25f, Action.DOWN))
        input.dispatch(mgr, scene)

        assertTrue(hud.consumed)       // HUD primero
        assertFalse(scene.consumed)    // escena no recibe
        assertFalse(globalConsumed)    // global no recibe
    }

    @Test fun scene_receives_when_hud_does_not_consume() {
        val mgr = HudManager()
        val scene = TestScene()
        val input = InputManager()

        // HUD fuera del área clicada
        val hud = TestHud { _, _ -> false }
        mgr.addLayer(hud)

        var globalConsumed = false
        input.addGlobalListener { ev -> if (ev is InputEvent.Touch) globalConsumed = true }

        input.post(InputEvent.Touch(80f, 80f, Action.DOWN))
        input.dispatch(mgr, scene)

        assertFalse(hud.consumed)
        assertTrue(scene.consumed)     // escena consume
        assertFalse(globalConsumed)    // global no recibe
    }

    @Test fun globals_receive_when_not_consumed() {
        val mgr = HudManager()
        val scene = object: TestScene() { override fun onInput(ev: InputEvent) = false }
        val input = InputManager()

        var called = false
        input.addGlobalListener { ev -> if (ev is InputEvent.Touch) called = true }

        input.post(InputEvent.Touch(10f, 10f, Action.DOWN))
        input.dispatch(mgr, scene)

        assertTrue(called)             // llegó a global
    }
}
