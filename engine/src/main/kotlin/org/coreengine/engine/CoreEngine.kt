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

package org.coreengine.engine

class CoreEngine(private val targetFps: Int = 60) {
    private val frameTime = 1_000_000_000L / targetFps
    private var running = false

    fun start(update: () -> Unit, render: () -> Unit) {
        running = true
        var lastTime = System.nanoTime()
        while (running) {
            val now = System.nanoTime()
            val delta = now - lastTime
            if (delta >= frameTime) {
                update()
                render()
                lastTime = now
            }
        }
    }

    fun stop() { running = false }
}
