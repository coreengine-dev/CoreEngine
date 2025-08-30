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

package org.coreengine.runtime.time

import org.coreengine.api.time.Clock
import java.util.concurrent.locks.LockSupport
import kotlin.math.max


class DefaultClock(private val targetFps: Int) : Clock {
    private val frameMs = 1000f / max(1, targetFps)
    private var lastNs = System.nanoTime()

    /** Devuelve dt en milisegundos (Float). */
    override fun tick(): Float {
        val now = System.nanoTime()
        val dtMs = (now - lastNs) / 1_000_000f
        lastNs = now
        return dtMs
    }

    /** Duerme hasta el próximo frame según targetFps. */
    override fun sleepToNextFrame() {
        val now = System.nanoTime()
        val dtMs = (now - lastNs) / 1_000_000f
        val remainingMs = frameMs - dtMs
        if (remainingMs > 0f) {
            val nanos = (remainingMs * 1_000_000L).toLong()
            LockSupport.parkNanos(nanos)
        }
        // resamplea después del sleep para reducir drift
        lastNs = System.nanoTime()
    }

    override fun nanoTime(): Long = System.nanoTime()
}