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