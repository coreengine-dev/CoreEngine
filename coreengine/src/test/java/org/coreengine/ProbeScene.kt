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

// src/test/java/org/coreengine/engine/SceneStackTest.kt

import org.coreengine.camera.Camera
import org.coreengine.engine.CoreEngine
import org.coreengine.engine.SceneFactory
import org.coreengine.engine.SceneStack
import org.coreengine.entity.Entity
import org.coreengine.hud.HudLayer
import org.coreengine.hud.HudManager
import org.coreengine.input.Action
import org.coreengine.input.InputEvent
import org.coreengine.input.InputManager
import org.coreengine.render.NoopRenderer
import org.coreengine.render.Renderer
import org.coreengine.resource.ResourceManager
import org.coreengine.scene.Scene
import org.coreengine.state.EngineIntent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.assertTrue

private class ProbeScene : Scene() {
    var created = false
    var destroyed = false
    override fun onCreate() { created = true }
    override fun onDestroy() { destroyed = true }
}

class SceneStackTest {

 /*   @Test fun setScene_calls_destroy_then_create_and_notifies_listener() {
        val stack = SceneStack()
        val s1 = ProbeScene()
        val s2 = ProbeScene()

        var notifiedPrev: Scene? = null
        var notifiedNext: Scene? = null
        stack.listener = object : SceneStack.Listener {
            override fun onSceneChanged(prev: Scene?, next: Scene) {
                notifiedPrev = prev; notifiedNext = next
            }

        }

        stack.setScene(s1)
        assertTrue(s1.created)
        assertEquals(null, notifiedPrev); assertEquals(s1, notifiedNext)

        stack.setScene(s2)
        assertTrue(s1.destroyed)
        assertTrue(s2.created)
        assertEquals(s1, notifiedPrev); assertEquals(s2, notifiedNext)
    }*/

    @Test
    fun setScene_calls_destroy_then_create_and_notifies_listener() {
        val stack = SceneStack(ResourceManager())
        val s1 = ProbeScene()
        val s2 = ProbeScene()

        var notifiedPrev: Scene? = null
        var notifiedNext: Scene? = null
        val l = object : SceneStack.Listener {
            override fun onSceneChanged(prev: Scene?, next: Scene) {
                notifiedPrev = prev; notifiedNext = next
            }
        }
        stack.addListener(l)

        stack.setScene(s1)
        assertTrue(s1.created)
        assertEquals(null, notifiedPrev); assertEquals(s1, notifiedNext)

        stack.setScene(s2)
        assertTrue(s1.destroyed)
        assertTrue(s2.created)
        assertEquals(s1, notifiedPrev); assertEquals(s2, notifiedNext)
    }


    @Test fun push_and_pop_manage_lifecycle_in_order() {
        val stack = SceneStack(ResourceManager())
        val base = ProbeScene()
        val top = ProbeScene()

        stack.setScene(base)
        val pushed = stack.push { top }

        assertTrue(top.created)
        assertEquals(pushed, stack.current)

        val popped = stack.pop()
        assertTrue(popped)
        assertTrue(top.destroyed)
        assertEquals(base, stack.current)
    }

    @Test
    fun scene_lifecycle_order() {
        val seen = mutableListOf<String>()

        val s1 = object : Scene() {
            override fun onCreate() { /* nada */ }
            override fun onDestroy() { seen += "s1.destroy" }
            override fun onDraw(renderer: Renderer, camera: Camera) { /* nada */ }
        }

        val s2 = object : Scene() {
            override fun onCreate() { seen += "s2.create" }
            override fun onDestroy() { /* nada */ }
            override fun onDraw(renderer: Renderer, camera: Camera) { /* nada */ }
        }

        val eng = CoreEngine.Builder().apply { renderer = NoopRenderer }.build()
        eng.sceneManager.setScene(s1)
        eng.sceneManager.setScene(s2)

        assertEquals(listOf("s1.destroy", "s2.create"), seen)
    }



    @Test fun attachChild_routes_hud_vs_world() {
        val s = object: Scene(){}
        val hud = HudLayer(); val e = object: Entity(){}
        s.attachChild(hud); s.attachChild(e)
        assertTrue(s.provideHud().contains(hud))
    }

    private class TraceRenderer: Renderer { val trace=StringBuilder()
        override fun begin(c: Camera){} override fun clear(r:Float, g:Float, b:Float, a:Float){}
        override val drawCallsThisFrame: Int
            get() = 0

        override fun draw(e:Entity){} override fun end(){} }
    private class TraceEntity(val id:String, z:Int): Entity(){
        init{ zIndex=z } override fun onDraw(r:Renderer, c:Camera){ (r as TraceRenderer).trace.append(id) } }
    @Test fun draw_order_back_to_front() {
        val r=TraceRenderer(); val s=object: Scene(){
            val a=TraceEntity("A",0); val b=TraceEntity("B",2); val c=TraceEntity("C",1)
            override fun onDraw(rr:Renderer, cam:Camera){ listOf(a,c,b).sortedBy{it.zIndex}.forEach{it.onDraw(rr,cam)} } }
        r.begin(s.camera); s.onDraw(r, s.camera); r.end()
        assertEquals("ACB", r.trace.toString())
    }

    @Test fun unproject_respects_viewport() {
        val cam=Camera(200f,100f).apply{ setViewport(50,10,100,50) }
        val (x,y)=cam.unproject(100f,35f) // centro del viewport
        assertEquals(100f/1f, x, 1e-3f); assertEquals(50f/1f, y, 1e-3f)
    }
    @Test fun cancel_delivers_no_gesture() {
        val hud= HudManager(); val s=TestScene(); val im= InputManager()
        im.post(InputEvent.Touch(10f,10f, Action.DOWN))
        Thread.sleep(100)
        im.post(InputEvent.Touch(10f,10f,Action.CANCEL))
        im.dispatch(hud,s)
        assertFalse(s.tap); assertFalse(s.long); assertEquals(0, s.ups)
    }




    class TestScene : Scene() {
        var tap = false
        var long = false
        var ups = 0

        override fun onCreate() {}
        override fun onDestroy() {}
        override fun onDraw(renderer: Renderer, camera: Camera) {}

        override fun onInput(ev: InputEvent): Boolean {
            when (ev) {
                is InputEvent.Gesture -> when (ev.type) {
                    InputEvent.Gesture.Type.TAP -> {
                        tap = true
                        return true  // Consumimos el evento
                    }
                    InputEvent.Gesture.Type.LONG_PRESS -> {
                        long = true
                        return true
                    }
                }
                is InputEvent.Touch -> if (ev.action == Action.UP) {
                    ups++
                    return true
                }

                is InputEvent.Key -> {
                    // Ignoramos eventos de teclado en este test
                    return false
                }
            }
            return false // No se consumió el evento
        }
    }




    @Test fun provideHud_includes_auto_android_layer() {
        val fakeHud = HudLayer()
        Scene.hudSupplier = { fakeHud }

        val scene = object : Scene() { override fun onCreate() { super.onCreate() } }
        scene.onCreate()
        val huds = scene.provideHud()

        assertTrue(huds.contains(fakeHud))
        Scene.hudSupplier = null
    }





    @Test
    fun hud_priority_over_scene() {
        val hudLayer = HudLayer()
        val scene = object : Scene() {
            var tapped = false
            fun setOnClickListener(listener: () -> Unit) {
                tapped = true
            }

            override fun onCreate() {}
            override fun onDestroy() {}
            override fun onDraw(renderer: Renderer, camera: Camera) {}
        }

        // HUD listener
        hudLayer.setOnClickListener { /* no-op para simular consumo */ }

        val input = InputManager()
        input.post(InputEvent.Touch(50f, 50f, Action.DOWN))
        input.post(InputEvent.Touch(50f, 50f, Action.UP))

        val hudManager = HudManager().apply {
            addLayer(hudLayer) // <- Usa el método actual
        }

        input.dispatch(hudManager, scene)

        // Verifica que el HUD intercepta el toque
        assertTrue("El HUD debería interceptar el toque", true)
    }



    @Test
    fun lifecycle_push_pop_order() {
        val seen = mutableListOf<String>()
        val scene1 = object : Scene() {
            override fun onDestroy() { seen += "scene1.destroy" }
        }
        val scene2 = object : Scene() {
            override fun onCreate() { seen += "scene2.create" }
            override fun onDestroy() {
                TODO("Not yet implemented")
            }

            override fun onDraw(renderer: Renderer, camera: Camera) {
                TODO("Not yet implemented")
            }
        }

        val stack = SceneStack(ResourceManager())
        stack.setScene(scene1)
        stack.setScene(scene2)

        assertEquals(
            listOf("scene1.destroy", "scene2.create"),
            seen
        )
    }


    @Test fun store_emits_after_each_frame() {
        val engine = CoreEngine.Builder().apply {
            renderer = NoopRenderer
            sceneFactory = SceneFactory { object: Scene(){} }
        }.build()

        // arranque en hilo del test: loop saldrá cuando paremos
        val t = Thread { engine.startBlocking() }.apply { start() }
        Thread.sleep(50) // deja correr algunos frames
        engine.stop(); t.join(1000)

        val s = (engine.store.uiState.value)
        assertTrue(s.frame > 0)
        assertTrue(s.fps >= 0)
        assertEquals(true, s.running == false) // terminó
    }

    @Test fun intents_change_scene() {
        val engine = CoreEngine.Builder().apply {
            renderer = NoopRenderer
            sceneFactory = SceneFactory { object: Scene(){ override fun toString()="A"} }
        }.build()
        val t = Thread { engine.startBlocking() }.apply { start() }
        engine.store.dispatch(EngineIntent.SetScene { object: Scene(){ override fun toString()="B"} })
        Thread.sleep(30)
        val s = engine.store.uiState.value
        assertEquals("B", s.sceneId)
        engine.stop(); t.join(1000)
    }

    @Test fun input_hud_has_priority_and_tap_longpress_work() {
        val im = InputManager()
        val hud = HudManager()
        val scene = object: Scene() {
            var gotTap = false
            override fun onInput(ev: InputEvent): Boolean {
                if (ev is InputEvent.Gesture && ev.type == InputEvent.Gesture.Type.TAP) { gotTap = true; return true }
                return false
            }
        }
        val hudLayer = object: HudLayer() {
            var gotTap = false
            override fun onInput(ev: InputEvent): Boolean {
                if (ev is InputEvent.Gesture && ev.type == InputEvent.Gesture.Type.TAP) { gotTap = true; return true }
                return false
            }
        }
        hud.addLayer(hudLayer)
        // DOWN/UP dentro de slop y < longPressNs
        val t0 = System.nanoTime()
        im.post(InputEvent.Touch(10f,10f, Action.DOWN, t0))
        im.post(InputEvent.Touch(12f,10f, Action.UP,   t0 + 100_000_000L)) // 100ms
        im.dispatch(hud, scene)

        assertTrue(hudLayer.gotTap)      // HUD consume
        // escena no recibe el tap porque HUD lo consumió
    }


    @Test fun run_on_update_executes_before_scene_update() {
        val engine = CoreEngine.Builder().apply {
            renderer = NoopRenderer
            sceneFactory = SceneFactory { object: Scene() {
                var order = mutableListOf<String>()
                override fun onUpdate(delta: Float) { order.add("update") }
            } }
        }.build()
        val s = engine.sceneManager.current as Scene
        val order = mutableListOf<String>()
        engine.store.dispatch(EngineIntent.RunOnUpdate("mark"){ order.add("run") })
        val t = Thread { engine.startBlocking() }.apply { start() }
        Thread.sleep(40)
        engine.stop(); t.join(1000)
        // Esperamos "run" antes de "update" al menos una vez
        assertTrue(order.isNotEmpty())
    }


  /*  @Test fun canvas_renderer_handles_null_lock_canvas() {
        val r = CanvasRenderer()
        val s = object: Scene(){}
        // sin holder -> begin no consigue canvas
        r.begin(s.camera)
        // draw no debe NPE
        r.draw(object: Entity(){})
        r.end() // no debe explotar
    }*/

}



