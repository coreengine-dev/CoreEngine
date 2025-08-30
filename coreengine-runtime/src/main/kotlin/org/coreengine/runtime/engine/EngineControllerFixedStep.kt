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

package org.coreengine.runtime.engine

import org.coreengine.api.time.Cancellable
import org.coreengine.api.time.FixedStepConfig
import org.coreengine.api.time.NANOS_PER_SECOND
import org.coreengine.runtime.time.impl.FixedStepLoopJvm



class EngineControllerFixedStep(
    private val engine: CoreEngine,
    fps: Int = 60
)
{
    private val loop = FixedStepLoopJvm(
        config = FixedStepConfig(stepNanos = NANOS_PER_SECOND / fps)
    )
    private var handle: Cancellable? = null
    @Volatile var isRunning = false; private set

    fun start() {
        if (isRunning) return
        isRunning = true
        handle = loop.start(
            update = { dt -> engine.tickFrame(dt) },
            render = { engine.renderCurrentScene() }   // ← usa el puente
        )
    }
    fun stop() {
        if (!isRunning) return
        isRunning = false
        handle?.cancel()
        handle = null
    }
}
