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


package org.coreengine.runtime.engine

import org.coreengine.api.hud.HudLayer
import org.coreengine.api.render.Renderer
import org.coreengine.api.resource.EngineServices
import org.coreengine.api.resource.ResourceManager
import org.coreengine.api.scene.Scene
import org.coreengine.api.time.Ticker
import org.coreengine.api.util.Logger
import org.coreengine.runtime.input.InputManager
import org.coreengine.runtime.render.SurfaceBackedRenderer
import org.coreengine.runtime.state.EngineIntent
import org.coreengine.runtime.state.EngineStore
import org.coreengine.runtime.state.EngineStoreImpl
import org.coreengine.runtime.time.FrameStats
import org.coreengine.runtime.util.ConsoleLogger
import org.coreengine.runtime.util.Debug

private fun sceneIdOf(s: Scene) = s.id

class CoreEngine private constructor(
    val config: EngineConfig,
    val renderer: Renderer,
    val sceneManager: SceneStack,
    val resourceManager: ResourceManager,
    val hudManager: HudManager,
    val inputManager: InputManager,
    private val fpsHudFactory: (() -> HudLayer)?,
) {

    val services: EngineServices = object : EngineServices {
        override val renderer get() = this@CoreEngine.renderer
        override val resourceManager get() = this@CoreEngine.resourceManager
        override val logger get() = Debug.logger
    }

    companion object {
        fun builder(): Builder = Builder()
    }


    // ---- Estado de ejecución
    @Volatile
    private var stopRequested = false
    @Volatile
    private var paused = false
    @Volatile
    private var running = false
    private var frameCount = 0L
    private var fpsHud: HudLayer? = null
    private val sampler = FrameStats()

    private val _storeImpl = EngineStoreImpl()
    val store: EngineStore get() = _storeImpl

    // ---- Frame loop (update + input + render + métricas)
    internal fun tickFrame(dt: Float) {
        // 0) Drenar intents (≤64)
        var pendingRunBlocks: MutableList<(Float) -> Unit>? = null
        var drained = 0
        while (drained++ < 64) {
            val polled = _storeImpl.intents.tryReceive().getOrNull() ?: break
            // TODO: reactivar intents cuando toque (Pause/Resume/SetScene/RunOnUpdate)
            if (polled is EngineIntent.RunOnUpdate) {
                (pendingRunBlocks ?: mutableListOf()).also { pendingRunBlocks = it }
                    .add(polled.block)
            }
        }

        // 1) Pausa/No running → heartbeat
        if (!running || paused) {
            sceneManager.current?.let { inputManager.dispatch(hudManager, it) }
            publish(frameDraws = 0, fpsNow = sampler.fpsHint(), dropped = 1)
            return
        }

        // 2) Escena actual
        val scene = sceneManager.current ?: run {
            publish(0, sampler.fpsHint(), dropped = 1)
            return
        }

        // 3) RunOnUpdate antes del update
        pendingRunBlocks?.forEach { it(dt) }
        pendingRunBlocks = null

        // 4) Update
        val tUpd0 = System.nanoTime()
        scene.onUpdate(dt)
        val msUpdate = (System.nanoTime() - tUpd0) / 1_000_000f

        // 5) Input después del update
        inputManager.dispatch(hudManager, scene)

        // 6) FPS
        val fpsNow = sampler.step(dt)

        // 7) Render
        var draws = 0
        var msRender = 0f
        var rendered = false
        val surfaceOk = (renderer as? SurfaceBackedRenderer)?.hasSurface ?: true
        try {
            if (surfaceOk) {
                val t0 = System.nanoTime()
                // Si tu renderer requiere begin/end, descomenta:
//                 renderer.begin(scene.camera) //relentiza los fps

                renderer.clear(0f, 0f, 0f, 1f)
                scene.onRender(renderer)
//                 hudManager.draw(renderer, scene.camera)

                msRender = (System.nanoTime() - t0) / 1_000_000f
                draws = renderer.drawCallsThisFrame
                rendered = true
            }
        } catch (t: Throwable) {
            Debug.e("Render frame failed: ${t.message}", t)
            stopRequested = true
            running = false
        } finally {
            // Llama end() una sola vez si tu renderer lo necesita.
            try { /* renderer.end() */
            } catch (_: Throwable) {
            }
        }


        // 8) Publicar SIEMPRE
        publish(
            frameDraws = if (rendered) draws else 0,
            fpsNow = fpsNow,
            dropped = if (rendered) 0 else 1,
            msUpd = msUpdate,
            msRen = msRender
        )
        Debug.i("tick dt=$dt")
    }

    // ---- Publicación de métricas/estado
    private fun sceneKey(): String =
        sceneManager.current?.let { sceneIdOf(it) } ?: (sceneManager.currentFactoryId ?: "boot")

    private fun publish(
        frameDraws: Int,
        fpsNow: Float,
        dropped: Int = 0,
        msUpd: Float = 0f,
        msRen: Float = 0f
    ) {
        frameCount += 1
        val heapKb = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
            .freeMemory()) / 1024).toInt()
        _storeImpl.update {
            it.copy(
                frame = frameCount,
                fps = fpsNow.toInt(),
                drawCalls = frameDraws,
                heapKb = heapKb,
                sceneId = sceneKey(),
                running = running && !paused && !stopRequested,
                droppedFrames = dropped
            )
        }
        // (fpsHud as? MetricsSink)?.onTimings(msUpd, msRen, frameDraws)
    }

    internal fun runLoop() {
        while (!stopRequested) {
            if (stopRequested || !running) break
        }
    }

    // ---- Builder
    class Builder {
        var config: EngineConfig = EngineConfig()
        var renderer: Renderer? = null
        var sceneFactory: SceneFactory? = null
        var fpsHudFactory: (() -> HudLayer)? = null
        var resourceSetup: ((ResourceManager) -> Unit)? = null
        var logger: Logger = ConsoleLogger()
        var beforeFirstScene: ((CoreEngine) -> Unit)? = null




        fun build(): CoreEngine {
            val resourceManager = ResourceManager()
            val scenes = SceneStack()
            val hud = HudManager()
            val input = InputManager()
            val r = renderer ?: error("Renderer requerido (Canvas/GL)")

            val engine = CoreEngine(
                config = config,
                renderer = r,
                sceneManager = scenes,
                resourceManager = resourceManager,
                hudManager = hud,
                inputManager = input,
                fpsHudFactory = fpsHudFactory
            )

            scenes.setServices(engine.services)

            Debug.logger = logger

            // Reflejar escena y recursos ante cambios
            scenes.addListener(object : SceneStack.Listener {
                override fun onSceneChanged(prev: Scene?, next: Scene) {
                    hud.clear()
                    engine.ensureFpsHud()
                    engine._storeImpl.update { st -> st.copy(sceneId = engine.sceneKey()) }
                    prev?.let { resourceManager.releaseOwnedBy(it.id) }
                }
            })

            // Hooks previos a primera escena
            resourceSetup?.invoke(resourceManager)
            beforeFirstScene?.invoke(engine)
            sceneFactory?.let { scenes.replace(it) }

            // Estado inicial
            engine._storeImpl.update {
                it.copy(
                    sceneId = scenes.current?.let { s -> sceneIdOf(s) } ?: "boot",
                    running = false
                )
            }
            return engine
        }
    }

    fun ensureFpsHud() {
        fpsHudFactory?.let {
            if (fpsHud == null) fpsHud = it()
            hudManager.addLayerIfAbsent(fpsHud as HudLayer)
        }
    }

    // ---- Controladores
    fun createControllerFixedStep(fps: Int = 60): EngineControllerFixedStep =
        EngineControllerFixedStep(this, fps)

    fun createController(ticker: Ticker): EngineController =
        EngineController(this, ticker)

    // ---- Arranque/paro bloqueante para tests
    fun startBlocking() {
        if (!running) {
            running = true
            paused = false
            stopRequested = false
            _storeImpl.update { it.copy(running = true) }
        }
        runLoop()
    }


    internal fun onControllerStart() {
        if (running) return
        running = true
        paused = false
        stopRequested = false
        _storeImpl.update { it.copy(running = true) }
    }

    internal fun onControllerStop() {
        running = false
        _storeImpl.update { it.copy(running = false) }
    }
}
