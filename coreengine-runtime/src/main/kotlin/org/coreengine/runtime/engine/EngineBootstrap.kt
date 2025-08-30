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


// ─────────────────────────────────────────────────
// Archivo: org/coreengine/engine/EngineBootstrap.kt
// Rol TCD: orquestación del ciclo 𝛂→…→𝝎 sin tocar tu Engine.
// Se integra desde MainActivity/AppController.
// ─────────────────────────────────────────────────
package api.coreengine.runtime.engine

import org.coreengine.engine.metrics.FrameMetrics
import org.coreengine.engine.metrics.FrameSampler
import org.coreengine.engine.profile.DeviceProfile
import org.coreengine.engine.profile.ProfileRunner
import org.coreengine.engine.metrics.CheckerReport
import org.coreengine.engine.metrics.FrameChecker
import org.coreengine.engine.metrics.BudgetEnforcer
import org.coreengine.scene.SceneManifest
import org.coreengine.state.EngineSnapshot
import org.coreengine.time.Strategy
import org.coreengine.time.UpdateNode
import org.coreengine.time.UpdateWorkList
import org.coreengine.util.Debug

// EngineBootstrap (versión segura)
class EngineBootstrap(
    private var profile: DeviceProfile,
    private val sceneIdProvider: () -> String,
    private val renderFunc: () -> Int
) : ProfileRunner {

    private val sampler = FrameSampler(profile.targetFps)

    private var manifest: SceneManifest = SceneManifest(id = "boot")
    private var enforcer: BudgetEnforcer = BudgetEnforcer(manifest)

    private val work = UpdateWorkList(Strategy.BFS)
    private var frame = 0L

    private var onSample: ((FrameMetrics) -> Unit)? = null

    // Checkers opcionales
    private val checkers = mutableListOf<FrameChecker>()
    private var onReport: ((CheckerReport) -> Unit)? = null

    override fun apply(profile: DeviceProfile) { this.profile = profile }

    fun setOnSample(cb: (FrameMetrics) -> Unit) { onSample = cb }
    fun addChecker(c: FrameChecker) { checkers += c }
    fun setOnReport(cb: (CheckerReport) -> Unit) { onReport = cb }

    fun setManifest(m: SceneManifest) {
        manifest = m
        enforcer = BudgetEnforcer(manifest)
    }

    fun pushUpdate(id: String, update: (Float) -> Unit) {
        work.push(UpdateNode(id, 0, update))
    }


    // Añade estos campos en la clase (una vez):
    private val counters: HashMap<String, Int> = hashMapOf()
    private val timersMs: HashMap<String, Float> = hashMapOf()
    private var lastTotalMem: Long = Runtime.getRuntime().totalMemory()
    private var logFrameCtr: Int = 0
    private var lastFps: Int = 0

    fun tick(dt: Float) {
        // 1) ID de escena estable
        val sid = sceneIdProvider()
        if (sid != manifest.id) {
            manifest = manifest.copy(id = sid)
            enforcer = BudgetEnforcer(manifest)
        }

        // 2) Update (sin inferencia de tipos ni invoke)
        val t0 = System.nanoTime()
        while (!work.isEmpty()) {
            val job = work.pop() ?: continue   // usa tu propio contenedor; pop() puede devolver null
            job.update(dt)                     // NO job.update.invoke(dt)
        }
        val msUpdate = (System.nanoTime() - t0) / 1_000_000f

        // 3) Render
        val r0 = System.nanoTime()
        val drawCalls: Int = renderFunc()
        val msRender = (System.nanoTime() - r0) / 1_000_000f

        // 4) Métricas y presupuesto
        val usedBytes: Long = lastTotalMem - Runtime.getRuntime().freeMemory()
        val metrics = sampler.sample(dt, msUpdate, msRender, drawCalls, usedBytes)
        lastFps = metrics.fps

        if (++logFrameCtr >= 60) { // throttle ~1 s a 60 fps
            enforcer.checkDrawCalls(drawCalls)?.let { Debug.w( "BudgetBreach: $it") }
            logFrameCtr = 0
        }
        val totalMs = msUpdate + msRender
        if (enforcer.checkFrameTime(totalMs)) throttle()

        // 5) Snapshot sin crear Maps por frame
        counters["drawCalls"] = drawCalls
        counters["heapKb"] = (usedBytes / 1024).toInt()
        counters["fps"] = lastFps

        timersMs["update"] = msUpdate
        timersMs["render"] = msRender

        val snap = EngineSnapshot(
            frame = ++frame,
            sceneId = manifest.id,
            counters = counters,
            timersMs = timersMs
        )

        // 6) Checkers (sin crear listas extra)
        if (checkers.isNotEmpty()) {
            for (c in checkers) {
                val reports = c.onFrame(snap)
                if (reports.isNotEmpty()) {
                    for (r in reports) {
                        onReport?.invoke(r)
                        Debug.w( "[${r.kind}] sev=${r.severity} ${r.details}")
                    }
                }
            }
        }

        onSample?.invoke(metrics)
    }



    fun tick1(dt: Float) {
        // Alinear id de escena sin tocar constructor
        val sid = sceneIdProvider()
        if (sid != manifest.id) {
            manifest = manifest.copy(id = sid)
            enforcer = BudgetEnforcer(manifest)
        }

        val t0 = System.nanoTime()
        while (!work.isEmpty()) work.pop()?.update?.invoke(dt)
        val msUpdate = (System.nanoTime() - t0) / 1_000_000f

        val r0 = System.nanoTime()
        val drawCalls = renderFunc()
        val msRender = (System.nanoTime() - r0) / 1_000_000f

        val alloc = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val metrics = sampler.sample(dt, msUpdate, msRender, drawCalls, alloc)

        enforcer.checkDrawCalls(drawCalls)?.let {
            Debug.w( "BudgetBreach: $it")
        }
        val total = msUpdate + msRender
        if (enforcer.checkFrameTime(total)) throttle()

        val snap = EngineSnapshot(
            frame = ++frame,
            sceneId = manifest.id,
            counters = mapOf(
                "drawCalls" to drawCalls,
                "heapKb" to (alloc / 1024).toInt(),
                "fps" to metrics.fps
            ),
            timersMs = mapOf("update" to msUpdate, "render" to msRender)
        )

        // Checkers (opcional)
        if (checkers.isNotEmpty()) {
            for (c in checkers) {
                val reports = c.onFrame(snap)
                if (reports.isNotEmpty()) {
                    reports.forEach { r ->
                        onReport?.invoke(r)
                        Debug.w( "[${r.kind}] sev=${r.severity} ${r.details}")
                    }
                }
            }
        }

        onSample?.invoke(metrics)
    }

    private fun throttle() {
        // Bajar LOD / saltar updates no críticos
    }
}


