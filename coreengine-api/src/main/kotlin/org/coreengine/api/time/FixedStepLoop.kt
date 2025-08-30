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

package org.coreengine.api.time

data class FixedStepConfig(
    val stepNanos: Long = NANOS_PER_SECOND / 60,
    val maxFrameClampNanos: Long = 250_000_000L // evita saltos > 250ms
)

interface FixedStepLoop {
    val config: FixedStepConfig
    /**
     * Arranca el loop determinista.
     * [update] se llama N veces por frame con dt fijo.
     * [render] se llama una vez por frame tras consumir updates.
     * Devuelve handle para parar.
     */
    fun start(update: (dtSeconds: Float) -> Unit, render: () -> Unit): Cancellable
}