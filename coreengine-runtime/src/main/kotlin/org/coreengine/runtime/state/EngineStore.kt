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

package org.coreengine.runtime.state

import kotlinx.coroutines.flow.StateFlow
import org.coreengine.runtime.engine.SceneFactory

interface EngineStore {
    val uiState: StateFlow<EngineUiState>
    fun dispatch(intent: EngineIntent)
}

sealed interface EngineIntent {
    data object Pause : EngineIntent
    data object Resume : EngineIntent
    data class SetScene(val factory: SceneFactory) : EngineIntent
    data class RunOnUpdate(val id: String = "anon", val block: (Float) -> Unit) : EngineIntent
}
