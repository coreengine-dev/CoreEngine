
/**
 * Entrada del usuario (toques/teclado).
 *
 * Flujo TCD:
 * - 𝚿: captura y cola de eventos de input por frame.
 * - ∇: despacho a escena activa (hit-test, gestos).
 * - 𝐌: respuesta visible tras update → render.
 *
 * TODO:
 * - Implementar cola interna (thread-safe).
 * - Soportar tipos de evento: Touch, Key, Gesture.
 * - Prioridad HUD→Scene.
 * - Hit-test básico (rects) y extensible (custom entity).
 */

// input/InputManager.kt
package org.coreengine.input

import org.coreengine.hud.HudManager
import org.coreengine.scene.Scene
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

class InputManager {
    // Cola lock-free para eventos crudos desde cualquier hilo
    private val queue = ConcurrentLinkedQueue<InputEvent>()
    // Señal para evitar trabajo extra cuando no hay eventos
    private val hasPending = AtomicBoolean(false)

    // —— NUEVO: listeners globales thread-safe ——
    private val global = CopyOnWriteArrayList<(InputEvent) -> Unit>()
    fun addGlobalListener(l: (InputEvent) -> Unit) { global += l }
    fun removeGlobalListener(l: (InputEvent) -> Unit) { global -= l }

    // --- Gestos (configurable) ---
    var longPressNs: Long = 500_000_000L // 500 ms
    var slopPx: Float = 16f              // en coordenadas de mundo

    // Estado de gesto actual (por puntero simple)
    private var downT = 0L
    private var downX = 0f
    private var downY = 0f
    private var moved = false

    /** Thread-safe. Encola un evento crudo (coords de pantalla). */
    fun post(event: InputEvent) {
        queue.add(event)
        hasPending.set(true)
    }

    /** Para tests, consulta rápida. */
    fun isEmpty(): Boolean = queue.isEmpty()

    /**
     * Consumir y despachar eventos del frame.
     * Llamar SOLO desde el hilo del engine.
     * Orden: HUD → Scene → Global listeners (si añades).
     */
 /*   fun dispatch(huds: HudManager?, scene: Scene?) {
        if (!hasPending.get()) return
        val cam = scene?.camera ?: run { drainSilently(); return }

        // Drenar cola actual
        while (true) {
            val raw = queue.poll() ?: break
            when (raw) {
                is InputEvent.Touch -> {
                    val (wx, wy) = cam.unproject(raw.x, raw.y)
                    when (raw.action) {
                        Action.DOWN -> {
                            downT = raw.timeNs
                            downX = wx; downY = wy; moved = false
                            if (emit(huds, scene, InputEvent.Touch(wx, wy, Action.DOWN, raw.timeNs))) continue
                        }
                        Action.MOVE -> {
                            if (!moved && (abs(wx - downX) > slopPx || abs(wy - downY) > slopPx)) moved = true
                            if (emit(huds, scene, InputEvent.Touch(wx, wy, Action.MOVE, raw.timeNs))) continue
                        }
                        Action.UP, Action.CANCEL -> {
                            // Gestos solo si no se movió más que el slop
                            if (raw.action == Action.UP) {
                                val dt = raw.timeNs - downT
                                val isLong = !moved && dt >= longPressNs
                                val isTap  = !moved && dt <  longPressNs
                                if (isLong && emit(huds, scene, InputEvent.Gesture(InputEvent.Gesture.Type.LONG_PRESS, wx, wy))) continue
                                if (isTap  && emit(huds, scene, InputEvent.Gesture(InputEvent.Gesture.Type.TAP,        wx, wy))) continue
                            }
                            emit(huds, scene, InputEvent.Touch(wx, wy, raw.action, raw.timeNs))
                            // Reset tras UP/CANCEL
                            downT = 0L; moved = false
                        }
                    }
                }
                else -> {
                    emit(huds, scene, raw)
                }
            }
        }
        hasPending.set(!queue.isEmpty())
    }*/

    fun dispatch(huds: HudManager?, scene: Scene?) {
        if (!hasPending.get()) return

        val cam = scene?.camera
        while (true) {
            val raw = queue.poll() ?: break
            var consumed = false

            if (cam == null) {
                // Sin cámara: no podemos unproject → notifica globales y sigue
                for (g in global) g(raw)
                continue
            }

            when (raw) {
                is InputEvent.Touch -> {
                    val (wx, wy) = cam.unproject(raw.x, raw.y)
                    when (raw.action) {
                        Action.DOWN -> {
                            downT = raw.timeNs
                            downX = wx; downY = wy; moved = false
                            consumed = emit(huds, scene, InputEvent.Touch(wx, wy, Action.DOWN, raw.timeNs))
                        }
                        Action.MOVE -> {
                            if (!moved && (kotlin.math.abs(wx - downX) > slopPx || kotlin.math.abs(wy - downY) > slopPx))
                                moved = true
                            consumed = emit(huds, scene, InputEvent.Touch(wx, wy, Action.MOVE, raw.timeNs))
                        }
                        Action.UP, Action.CANCEL -> {
                            if (raw.action == Action.UP) {
                                val dt = raw.timeNs - downT
                                val isLong = !moved && dt >= longPressNs
                                val isTap  = !moved && dt <  longPressNs
                                if (!consumed && isLong)
                                    consumed = emit(huds, scene, InputEvent.Gesture(InputEvent.Gesture.Type.LONG_PRESS, wx, wy))
                                if (!consumed && isTap)
                                    consumed = emit(huds, scene, InputEvent.Gesture(InputEvent.Gesture.Type.TAP, wx, wy))
                            }
                            if (!consumed)
                                consumed = emit(huds, scene, InputEvent.Touch(wx, wy, raw.action, raw.timeNs))
                            downT = 0L; moved = false
                        }
                    }
                }
                else -> {
                    consumed = emit(huds, scene, raw)
                }
            }

            // Notificar globales si NADIE consumió
            if (!consumed) for (g in global) g(raw)
        }

        hasPending.set(!queue.isEmpty())
    }


    private fun emit(huds: HudManager?, scene: Scene?, ev: InputEvent): Boolean {
        if (huds?.onInput(ev) == true) return true
        if (scene?.onInput(ev) == true) return true
        return false
    }

    private fun drainSilently() {
        if (!hasPending.get()) return
        while (queue.poll() != null) { /* drop */ }
        hasPending.set(false)
    }
}



// =============================
// Contratos
// =============================
/**
 * Contrato opcional para escenas y capas HUD.
 * Devuelven true si consumen el evento.
 */
interface InputListener {
    fun onInput(ev: InputEvent): Boolean
}

interface Clickable {
    fun setOnClickListener(listener: (() -> Unit)?)
}

