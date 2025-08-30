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

package api.coreengine.runtime.state

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class EngineStoreImpl(
    initial: EngineUiState = EngineUiState()
) : EngineStore {
    private val _state = MutableStateFlow(initial)
    override val uiState: StateFlow<EngineUiState> get() = _state

    // Procesa intents en el hilo del engine (pull no bloqueante en cada frame)
    internal val intents = Channel<EngineIntent>(capacity = Channel.UNLIMITED)

    override fun dispatch(intent: EngineIntent) {
        intents.trySend(intent)
    }

    internal fun update(reducer: (EngineUiState) -> EngineUiState) {
        _state.value = reducer(_state.value)
    }
}
