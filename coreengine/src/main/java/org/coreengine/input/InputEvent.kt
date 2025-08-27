package org.coreengine.input

/** Evento genérico de input. Extensible. */
sealed class InputEvent {
    data class Touch(
        val x: Float,
        val y: Float,
        val action: Action,
        val timeNs: Long = System.nanoTime()   // ← marca al postear
    ) : InputEvent()
    data class Key(val keyCode: Int, val pressed: Boolean) : InputEvent()
    data class Gesture(val type: Type, val x: Float, val y: Float) : InputEvent() {
        enum class Type { TAP, LONG_PRESS }
    }
}


// =============================
// Eventos
// =============================

/** Acciones posibles de toque. */
enum class Action { DOWN, MOVE, UP, CANCEL }