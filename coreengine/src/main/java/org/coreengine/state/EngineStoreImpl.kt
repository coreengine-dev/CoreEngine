package org.coreengine.state

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
