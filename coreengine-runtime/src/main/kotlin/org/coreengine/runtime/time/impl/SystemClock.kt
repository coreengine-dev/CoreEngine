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

package org.coreengine.runtime.time.impl

import org.coreengine.api.time.Clock

object SystemClock : Clock {
    private var lastNs: Long = System.nanoTime()

    override fun nanoTime(): Long = System.nanoTime()

    /** dt en segundos desde la última llamada. */
    override fun tick(): Float {
        val now = System.nanoTime()
        val dtSec = (now - lastNs) / 1_000_000_000f
        lastNs = now
        return dtSec
    }

    /** Sin objetivo de FPS aquí. No duerme. */
    override fun sleepToNextFrame() { /* no-op */ }

    /** Opcional: re-sincroniza para evitar picos tras pausas. */
    fun resync() { lastNs = System.nanoTime() }
}
