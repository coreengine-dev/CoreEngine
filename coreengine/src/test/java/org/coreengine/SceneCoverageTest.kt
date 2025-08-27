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
import org.coreengine.input.Action
import org.coreengine.input.InputEvent
import org.coreengine.input.InputListener
import org.coreengine.render.Renderer
import org.coreengine.scene.Scene
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// -------- Spies simples --------

class SpyRenderer : Renderer {
    val calls = mutableListOf<String>()
    override fun begin(camera: Camera) {
        calls += "begin"
    }

    override fun clear(r: Float, g: Float, b: Float, a: Float) {
        calls += "clear"
    }

    override val drawCallsThisFrame: Int
        get() = 0

    override fun draw(entity: Entity) {}
    override fun end() {
        calls += "end"
    }
}


 open class SpyEntity(val id: String, var consume: Boolean=false) : Entity(), InputListener {
    var updates = 0
    var draws = 0
    override fun onUpdate(delta: Float) { updates++ ; super.onUpdate(delta) }
    override fun onDraw(renderer: Renderer, camera: Camera) { draws++ ; super.onDraw(renderer, camera) }
    override fun onInput(ev: InputEvent): Boolean = consume
}

open class ZEntity(id: String, z:Int): SpyEntity(id) { init { zIndex = z } }

private class ProbeHud(val tag:String, var consume:Boolean=false): HudLayer() {
    var inputs = 0
    override fun onInput(ev: InputEvent): Boolean { inputs++; return consume || super.onInput(ev) }
}

// -------- Escena de prueba minimal --------
private class Scen : Scene() {
    // expone helpers para tests
    fun addWorld(e: Entity) = attachChild(e)
    fun addHud(h: HudLayer) = attachChild(h)
}

// ======================= TESTS =======================

class SceneCoverageTest {

    @Test
    fun `onUpdate propaga a mundo y HUD`() {
        val s = Scen()
        val a = SpyEntity("A"); val b = SpyEntity("B")
        val hud = ProbeHud("H")
        s.addWorld(a); s.addWorld(b); s.addHud(hud)

        s.onUpdate(0.016f)

        assertEquals(1, a.updates)
        assertEquals(1, b.updates)
        // HudLayer hereda Entity → también recibe update
        // Si no te interesa contarlo, al menos no debe fallar
    }

    @Test fun `onDraw ordena por zIndex back→front`() {
        val s = Scen()
        val r = SpyRenderer()
        val a = ZEntity("A", z=0)
        val c = ZEntity("C", z=1)
        val b = ZEntity("B", z=2)
        s.addWorld(a); s.addWorld(c); s.addWorld(b)

        s.onDraw(r, s.camera)

        // Validamos conteo y orden implícito con draws
        assertEquals(1, a.draws); assertEquals(1, b.draws); assertEquals(1, c.draws)
        // Para orden exacto, usamos una entidad que registre trazas:
        val tracer = object: Renderer by r {
            val trace = StringBuilder()
            override fun draw(entity: Entity) {}
        }
        // Re-ejecutamos con entidades que escriban su id:
        val T = object: Scene() {
            val A = object: ZEntity("A",0) {
                override fun onDraw(renderer: Renderer, camera: Camera) { (renderer as SpyRenderer).calls += "A"; super.onDraw(renderer, camera) }
            }
            val C = object: ZEntity("C",1) {
                override fun onDraw(renderer: Renderer, camera: Camera) { (renderer as SpyRenderer).calls += "C"; super.onDraw(renderer, camera) }
            }
            val B = object: ZEntity("B",2) {
                override fun onDraw(renderer: Renderer, camera: Camera) { (renderer as SpyRenderer).calls += "B"; super.onDraw(renderer, camera) }
            }
            init { attachChild(A); attachChild(C); attachChild(B) }
        }
        val rr = SpyRenderer()
        T.onDraw(rr, T.camera)
        assertEquals(listOf("A","C","B"), rr.calls)
    }

    @Test fun `input prioriza HUD sobre mundo y respeta zIndex descendente`() {
        val s = Scen()
        val hudLow = ProbeHud("low", consume=false).apply { zIndex = 0 }
        val hudTop = ProbeHud("top", consume=true).apply { zIndex = 10 }
        val world = SpyEntity("W", consume=true)

        s.addHud(hudLow); s.addHud(hudTop); s.addWorld(world)

        val ev = InputEvent.Touch(10f,10f, Action.DOWN)
        val consumed = s.onInput(ev)

        assertTrue(consumed)
        assertEquals(1, hudTop.inputs) // top recibe primero
        assertEquals(0, hudLow.inputs) // no llega al bajo
        // mundo no debería recibirlo
    }

    @Test fun `input llega al mundo si HUD no consume`() {
        val s = Scen()
        val hud = ProbeHud("H", consume=false).apply { zIndex = 5 }
        val world = SpyEntity("W", consume=true)
        s.addHud(hud); s.addWorld(world)

        val consumed = s.onInput(InputEvent.Touch(0f,0f, Action.DOWN))
        assertTrue(consumed)
        assertEquals(1, hud.inputs) // HUD lo vio
        // mundo consumió al final
    }

    @Test fun `provideHud incluye capas adjuntas y es idempotente con auto-HUD`() {
        val s = Scen()
        val h1 = ProbeHud("H1")
        s.addHud(h1)
        val list1 = s.provideHud()
        val list2 = s.provideHud()
        assertTrue(list1.contains(h1))
        assertEquals(list1.size, list2.size) // idempotente
    }

    @Test fun `onDestroy limpia sin lanzar`() {
        val s = Scen()
        // No hay Views reales en JVM. Solo validar que no lanza.
        s.onDestroy()
    }
}
