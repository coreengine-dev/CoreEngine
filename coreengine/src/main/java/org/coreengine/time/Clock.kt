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

package org.coreengine.time

/**
 * Clock — Fuente de tiempo basal (𝛂).
 *
 * Función:
 * - Marca el dt (deltaTime) por frame en segundos.
 * - Garantiza estabilidad (clamp para evitar saltos).
 * - TCD: simboliza la vibración basal α → tiempo discreto.
 */

import java.util.concurrent.locks.LockSupport

class Clock(private val targetFps: Int = 60, private val maxDelta: Float = 0.1f) {
    private val frameNs: Long = 1_000_000_000L / targetFps
    private var last: Long = System.nanoTime()
    private var nextDeadline: Long = last + frameNs

    fun tick(): Float {
        val now = System.nanoTime()
        val dt = ((now - last) / 1_000_000_000.0).toFloat().coerceAtMost(maxDelta)
        last = now
        return dt
    }

    fun sleepToNextFrame() {
        // programar por deadline absoluto
        var remaining = nextDeadline - System.nanoTime()

        // si vamos tarde, realinear sin dormir
        if (remaining <= 0L) {
            val behind = -remaining
            val skips = 1 + (behind / frameNs)
            nextDeadline += skips * frameNs
            return
        }

        // sueño grueso dejando ~0.3 ms para afinar
        if (remaining > 300_000L) {
            LockSupport.parkNanos(remaining - 300_000L)
        }

        // spin corto para afinar al deadline
        while (true) {
            if (System.nanoTime() >= nextDeadline) break
            // no yield; empeora precisión en muchas JVM
        }

        // avanzar deadline para el siguiente frame
        nextDeadline += frameNs
    }

    fun reset() {
        last = System.nanoTime()
        nextDeadline = last + frameNs
    }
}



