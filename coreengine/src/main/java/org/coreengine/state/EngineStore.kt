package org.coreengine.state

import org.coreengine.engine.SceneFactory
import kotlinx.coroutines.flow.StateFlow

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
