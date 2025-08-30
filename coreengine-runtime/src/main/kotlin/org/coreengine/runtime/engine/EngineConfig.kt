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

import org.coreengine.api.render.RenderBackend


/**
 * Configuración del motor.
 * TCD: 𝛂 (tiempo base) influye en targetFps/vsync.
 */
data class EngineConfig(
    val targetFps: Int = 60,
    val useVSync: Boolean = true,
    val renderBackend: RenderBackend = RenderBackend.CANVAS
)