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
import org.coreengine.entity.Entity
import org.coreengine.hud.HudLayer
import org.coreengine.input.InputEvent
import org.coreengine.input.Action
import org.coreengine.render.Renderer
import org.coreengine.scene.Scene
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

// ---- Dummies ----
private class NoopRenderer : Renderer {
    override fun begin(camera: Camera) {}
    override fun clear(r: Float, g: Float, b: Float, a: Float) {}
    override val drawCallsThisFrame: Int
        get() = 0

    override fun draw(entity: Entity) {}
    override fun end() {}
}
private open class E(val name:String, private val consume:Boolean=false): Entity() {
    var inputs=0
    override fun onInput(ev: InputEvent): Boolean { inputs++; return consume }
}

// Escena concreta con acceso a helpers de attach/detach/provideHud
private class S : Scene() {
    fun addWorld(e: Entity) = attachChild(e)
    fun addHud(h: HudLayer) = attachChild(h)
    fun remove(node: Entity) = detachChild(node)
}

class SceneExtraCoverageTest {

    @Test fun `attachChild separa mundo vs HUD e idempotencia de provideHud`() {
        val s = S()
        val hud = HudLayer()
        val w = E("W")
        s.addHud(hud); s.addWorld(w)

        val h1 = s.provideHud()
        val h2 = s.provideHud()

        assertTrue(h1.contains(hud))
        assertEquals(h1.size, h2.size) // idempotente
    }

    @Test
    fun `detachChild quita correctamente de su colección`() {
        val s = S()
        val hud = HudLayer()
        val w = E("W")
        s.addHud(hud); s.addWorld(w)

        s.remove(hud); s.remove(w)

        // no debe estar en provideHud ni volver a dibujarse
        assertFalse(s.provideHud().contains(hud))
    }

    @Test fun `onInput orden HUD→mundo (mundo sólo recibe si HUD no consume)`() {
        val s = S()
        val hud = object : HudLayer() {
            var seen=0; var consume=false
            override fun onInput(ev: InputEvent): Boolean { seen++; return consume }
        }
        val w = E("W", consume = true)
        s.addHud(hud); s.addWorld(w)

        // caso 1: HUD consume
        hud.consume = true
        assertTrue(s.onInput(InputEvent.Touch(0f,0f, Action.DOWN)))
        assertEquals(1, hud.seen); assertEquals(0, w.inputs)

        // caso 2: HUD no consume → llega a mundo
        hud.consume = false
        assertTrue(s.onInput(InputEvent.Touch(0f,0f, Action.DOWN)))
        assertEquals(2, hud.seen); assertEquals(1, w.inputs)
    }

    @Test fun `onDraw respeta zIndex back→front`() {
        val s = S()
        val a = object: E("A") { init { zIndex = 0 } }
        val c = object: E("C") { init { zIndex = 1 } }
        val b = object: E("B") { init { zIndex = 2 } }
        val trace = StringBuilder()
        val r = object: Renderer by NoopRenderer() {
            override fun draw(entity: Entity) { /* no-op */ }
        }
        // imprimimos orden sobreescribiendo onDraw de cada entidad
        val AA = object: E("A"){ init { zIndex=0 }
            override fun onDraw(renderer: Renderer, camera: Camera){ trace.append("A"); super.onDraw(renderer, camera) } }
        val CC = object: E("C"){ init { zIndex=1 }
            override fun onDraw(renderer: Renderer, camera: Camera){ trace.append("C"); super.onDraw(renderer, camera) } }
        val BB = object: E("B"){ init { zIndex=2 }
            override fun onDraw(renderer: Renderer, camera: Camera){ trace.append("B"); super.onDraw(renderer, camera) } }

        s.addWorld(AA); s.addWorld(CC); s.addWorld(BB)
        s.onDraw(r, s.camera)

        assertEquals("ACB", trace.toString())
    }

    @Test fun `onInput devuelve false si nadie consume`() {
        val s = S()
        val hud = object : HudLayer() {}
        val w = E("W", consume = false)
        s.addHud(hud); s.addWorld(w)

        val consumed = s.onInput(InputEvent.Touch(0f,0f, Action.DOWN))
        assertFalse(consumed)
    }
}
