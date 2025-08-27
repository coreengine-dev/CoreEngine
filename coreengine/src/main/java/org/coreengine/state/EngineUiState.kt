package org.coreengine.state

data class EngineUiState(
    val frame: Long = 0,
    val fps: Int = 0,
    val drawCalls: Int = 0,
    val heapKb: Int = 0,
    val sceneId: String = "unknown",
    val running: Boolean = false,
    val droppedFrames: Int = 0
)