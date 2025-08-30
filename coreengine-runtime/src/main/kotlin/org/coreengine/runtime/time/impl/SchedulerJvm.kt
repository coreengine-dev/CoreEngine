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
import org.coreengine.api.time.Scheduler
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class SchedulerJvm(
    private val pool: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "CoreEngine-Scheduler").apply { isDaemon = true }
    }
) : Scheduler {

    override fun scheduleAtFixedRate(periodNanos: Long, task: (nowNanos: Long) -> Unit): Cancellable {
        val f = pool.scheduleAtFixedRate(
            { task(System.nanoTime()) },
            0L,
            periodNanos,
            TimeUnit.NANOSECONDS
        )
        return Cancellable { f.cancel(false) }
    }

    override fun scheduleOnce(delayNanos: Long, task: () -> Unit): Cancellable {
        val f = pool.schedule(task, delayNanos, TimeUnit.NANOSECONDS)
        return Cancellable { f.cancel(false) }
    }
}