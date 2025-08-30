package org.corestudio.ide.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

@Service(Service.Level.PROJECT)
class EngineService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val running = AtomicBoolean(false)
    private lateinit var project: Project

    fun init(project: Project) { this.project = project }

    fun start() {
        if (running.compareAndSet(false, true)) {
            scope.launch {
                val frameMs = 16L
                while (running.get()) {
                    // α→Φ→Ω→M→Ψ→ω (placeholder). Publica telemetría.
                    TelemetryBus.publish(drawCalls = 0, fps = 60, msUpdate = 1.0, msRender = 2.0)
                    delay(frameMs)
                }
            }
        }
    }

    fun stop() { running.set(false) }
    fun hotReload() { /* recarga materiales/escena */ }
}
