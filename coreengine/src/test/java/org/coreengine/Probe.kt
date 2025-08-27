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


import org.coreengine.camera.Camera
import org.coreengine.hud.HudLayer
import org.coreengine.hud.HudManager
import org.coreengine.input.Action
import org.coreengine.input.InputEvent
import org.coreengine.input.InputManager
import org.coreengine.scene.Scene
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

private class Probe : HudLayer() {
    var tap=false; var long=false; var ups=0
    override fun onInput(ev: InputEvent): Boolean {
        when (ev) {
            is InputEvent.Gesture -> {
                if (ev.type==InputEvent.Gesture.Type.TAP) { tap=true; return true }
                if (ev.type==InputEvent.Gesture.Type.LONG_PRESS) { long=true; return true }
            }
            is InputEvent.Touch -> if (ev.action== Action.UP) { ups++ }
            is InputEvent.Key -> TODO()
        }
        return false
    }
}



open class TestScene: Scene() {
    override val camera = Camera(100f,100f).apply { setViewport(0,0,100,100) }
    var tap=false; var long=false; var ups=0
    var consumed = false
    override fun onInput(ev: InputEvent): Boolean {
        when (ev) {
            is InputEvent.Gesture -> {
                if (ev.type==InputEvent.Gesture.Type.TAP) { tap=true; return true }
                if (ev.type==InputEvent.Gesture.Type.LONG_PRESS) { long=true; return true }
            }
            is InputEvent.Touch -> {
                if (ev.action==Action.UP) { ups++ }
                if (ev.action == Action.DOWN) {
                    consumed = true
                    return true
                }
            }

            is InputEvent.Key -> TODO()

        }
        return false
    }
}

class GestureTest {

    @Test fun tap_emits_gesture_consumed_by_hud_first() {
        val hudMgr = HudManager()
        val scene = TestScene()
        val input = InputManager()
        val hud = Probe()
        hudMgr.addLayer(hud)

        input.post(InputEvent.Touch(10f,10f,Action.DOWN))
        Thread.sleep(50)
        input.post(InputEvent.Touch(10f,10f,Action.UP))
        input.dispatch(hudMgr, scene)

        assertTrue(hud.tap)
        assertFalse(scene.tap)
        assertEquals(0, hud.ups) // se consumió como gesto, no llegó UP
    }

    @Test fun long_press_emits_gesture_when_no_move() {
        val hudMgr = HudManager()
        val scene = TestScene()
        val input = InputManager()

        input.post(InputEvent.Touch(10f,10f,Action.DOWN))
        Thread.sleep(600) // >500ms
        input.post(InputEvent.Touch(10f,10f,Action.UP))
        input.dispatch(hudMgr, scene)

        assertTrue(scene.long)
        assertEquals(0, scene.ups)
    }

    @Test
    fun move_over_slop_cancels_gesture_and_delivers_up() {
        val hudMgr = HudManager()
        val scene = TestScene()
        val input = InputManager()

        input.post(InputEvent.Touch(10f,10f,Action.DOWN))
        input.post(InputEvent.Touch(40f,10f,Action.MOVE)) // > slop
        input.post(InputEvent.Touch(40f,10f,Action.UP))
        input.dispatch(hudMgr, scene)

        assertFalse(scene.tap)
        assertFalse(scene.long)
        assertEquals(1, scene.ups) // cae como TOUCH UP normal
    }
}
