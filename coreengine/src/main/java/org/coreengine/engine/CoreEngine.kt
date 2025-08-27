/**
 * Núcleo del motor.
 *
 * Ciclo por frame:
 *   dt = clock.tick()
 *   → (intents) drenar EngineIntent*  [UDF: control]
 *   → (run-on-update) ejecutar bloques seguros en hilo del engine
 *   → input.dispatch(hud, scene)      [𝚿: input → HUD → Scene]
 *   → scene.onUpdate(dt)              [∇: lógica]
 *   → renderer.begin(camera)
 *        renderer.clear()
 *        scene.onDraw(renderer, camera)
 *        hudManager.draw(renderer, camera)
 *     renderer.end()                  [𝐌: render]
 *   → publicar EngineUiState          [𝝎: observación]
 *
 * Flags:
 *  - running: “debe avanzar frames” (no implica hilo vivo)
 *  - paused:  “no avanzar frames” pero seguir drenando intents
 *  - stopRequested: terminar loop (hilo sale)
 */

package org.coreengine.engine

import org.coreengine.hud.HudLayer
import org.coreengine.hud.HudManager
import org.coreengine.hud.MetricsSink
import org.coreengine.input.InputManager
import org.coreengine.render.Renderer
import org.coreengine.render.SurfaceBackedRenderer
import org.coreengine.resource.ResourceManager
import org.coreengine.scene.Scene
import org.coreengine.state.EngineIntent
import org.coreengine.state.EngineStore
import org.coreengine.state.EngineStoreImpl
import org.coreengine.time.Clock
import org.coreengine.time.FrameStats
import org.coreengine.time.ThreadTicker
import org.coreengine.time.Ticker
import org.coreengine.util.ConsoleLogger
import org.coreengine.util.Debug
import org.coreengine.util.Logger

private fun sceneIdOf(s: Scene) = s.toString()

class CoreEngine private constructor(
    val config: EngineConfig,
    val renderer: Renderer,
    val sceneManager: SceneStack,
    val resourceManager: ResourceManager,
    val hudManager: HudManager,
    val inputManager: InputManager,
    val clock: Clock,
    private val fpsHudFactory: (() -> HudLayer)?,
)
{

    internal fun tickFrame(dt: Float) {
        // 0) Drenar intents (≤64) y reflejar sceneId si cambia
        var pendingRunBlocks: MutableList<(Float) -> Unit>? = null
        var drained = 0
        while (drained++ < 64) {
            val polled = _storeImpl.intents.tryReceive().getOrNull() ?: break
            when (polled) {
                is EngineIntent.Pause -> pause()
                is EngineIntent.Resume -> resume()
                is EngineIntent.SetScene -> {
                    sceneManager.replace(polled.factory)
                    _storeImpl.update { st -> st.copy(sceneId = sceneKey()) }
                }
                is EngineIntent.RunOnUpdate -> {
                    (pendingRunBlocks ?: mutableListOf()).also { pendingRunBlocks = it }.add(polled.block)
                }
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

        // 3) RunOnUpdate ANTES del update
        pendingRunBlocks?.forEach { it(dt) }
        pendingRunBlocks = null

        // 4) Update
        val tUpd0 = System.nanoTime()
        scene.onUpdate(dt)
        val msUpdate = (System.nanoTime() - tUpd0) / 1_000_000f

        // 5) Input DESPUÉS del update
        inputManager.dispatch(hudManager, scene)

        // 6) FPS
        val fpsNow = sampler.step(dt)

        // 7) Render
        var draws = 0
        var msRender = 0f
        var rendered = false
        try {
            renderer.begin(scene.camera)
//            val canDraw = (renderer as? CanvasRenderer)?.hasCanvas != false
            val canDraw = (renderer as? SurfaceBackedRenderer)?.hasSurface != false

            if (canDraw) {
                val tRen0 = System.nanoTime()
                renderer.clear(0f, 0f, 0f, 1f)
                scene.onDraw(renderer, scene.camera)
                hudManager.draw(renderer, scene.camera)
                msRender = (System.nanoTime() - tRen0) / 1_000_000f
                draws = renderer.drawCallsThisFrame
                rendered = true
            }
        } catch (t: Throwable) {
            Debug.e("Render frame failed: ${t.message}", t)
            stopRequested = true
            running = false
        } finally {
            try { renderer.end() } catch (_: Throwable) {}
        }

        // 8) Publicar SIEMPRE
        publish(
            frameDraws = if (rendered) draws else 0,
            fpsNow = fpsNow,
            dropped = if (rendered) 0 else 1,
            msUpd = msUpdate,
            msRen = msRender
        )
    }




    fun createController(): EngineController = EngineController(this)

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

    fun start() {
        if (running) return
        running = true
        paused = false
        stopRequested = false
        _storeImpl.update { it.copy(running = true) }
//        loop()
    }

    fun startBlocking() {
        if (!running) {
            running = true
            paused = false
            stopRequested = false
            _storeImpl.update { it.copy(running = true) }
        }
        loop() // corre el bucle en el hilo del test
    }



    fun stop() {
        stopRequested = true
        running = false
        _storeImpl.update { it.copy(running = false) }
    }

    fun pause() {
        paused = true
        running = false
        _storeImpl.update { it.copy(running = false) }
    }

    fun resume() {
        paused = false
        running = true
        _storeImpl.update { it.copy(running = true) }
    }

    // ---------- helpers de publicación ----------
    private fun sceneKey(): String =
        sceneManager.current?.toString() ?: (sceneManager.currentFactoryId ?: "boot")


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
        (fpsHud as? MetricsSink)?.onTimings(msUpd, msRen, frameDraws)

    }

    private fun loop() {
        while (!stopRequested) {
            val dt = clock.tick()
            tickFrame(dt)
            if (stopRequested || !running) break
            clock.sleepToNextFrame()
        }
    }



    internal fun runLoop() = loop()

    // ------------------------------------------------
    //                    BUILDER
    // ------------------------------------------------
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
            val scenes = SceneStack(resourceManager)
            val hud = HudManager()
            val input = InputManager()
            val clock = Clock(config.targetFps)
            val r = renderer ?: error("Renderer requerido (Canvas/GL)")

            val engine =
                CoreEngine(config, r, scenes, resourceManager, hud, input, clock, fpsHudFactory)

            Debug.logger = logger

            engine._storeImpl.update {
                it.copy(
                    sceneId = scenes.current?.let { s -> sceneIdOf(s) } ?: "boot",
                    running = false
                )
            }

            scenes.addListener(object : SceneStack.Listener {
                override fun onSceneChanged(prev: Scene?, next: Scene) {
                    hud.clear()
                    prev?.provideHud()?.forEach { hud.removeLayer(it) }
                    next.provideHud().forEach { hud.addLayer(it) }
                    engine.ensureFpsHud()
                    // reflejar sceneId usando la clave estable (factoryId preferente)
                    engine._storeImpl.update { st -> st.copy(sceneId = engine.sceneKey()) }
                    // liberar recursos por id estable
                    prev?.let { resourceManager.releaseOwnedBy(it.id) }
                }
            })

            // hooks previos a primera escena
            resourceSetup?.invoke(resourceManager)
            sceneFactory?.let { scenes.replace(it) }


            return engine
        }
    }

    fun ensureFpsHud() {
        fpsHudFactory?.let {
            if (fpsHud == null) fpsHud = it()
            hudManager.addLayerIfAbsent(fpsHud as HudLayer)
        }
    }

    fun createController(
        tickerFactory: (Clock) -> Ticker = { c -> ThreadTicker(c) }
    ): EngineController = EngineController(this, tickerFactory)
}
