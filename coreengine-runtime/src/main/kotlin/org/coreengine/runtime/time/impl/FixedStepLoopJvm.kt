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

import org.coreengine.api.time.Cancellable
import org.coreengine.api.time.Clock
import org.coreengine.api.time.FixedStepConfig
import org.coreengine.api.time.FixedStepLoop
import org.coreengine.api.time.NANOS_PER_SECOND
import org.coreengine.api.time.Scheduler


class FixedStepLoopJvm(
    private val clock: Clock = SystemClock,
    override val config: FixedStepConfig = FixedStepConfig(),
    private val scheduler: Scheduler = SchedulerJvm()
) : FixedStepLoop {

    override fun start(update: (dtSeconds: Float) -> Unit, render: () -> Unit): Cancellable {
        var running = true
        val step = config.stepNanos
        val dtSeconds = step.toFloat() / NANOS_PER_SECOND
        var previous = clock.nanoTime()
        var accumulator = 0L

        val handle = scheduler.scheduleAtFixedRate(step) {
            if (!running) return@scheduleAtFixedRate
            var now = clock.nanoTime()
            var frame = now - previous
            if (frame > config.maxFrameClampNanos) frame = config.maxFrameClampNanos
            previous = now
            accumulator += frame

            while (accumulator >= step && running) {
                update(dtSeconds)
                accumulator -= step
            }
            render()
        }
        return Cancellable {
            running = false
            handle.cancel()
        }
    }
}