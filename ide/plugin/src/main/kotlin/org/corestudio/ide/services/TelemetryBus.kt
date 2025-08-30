package org.corestudio.ide.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Telemetry(val fps:Int, val drawCalls:Int, val msUpdate:Double, val msRender:Double)

object TelemetryBus {
    private val _state = MutableStateFlow(Telemetry(0,0,0.0,0.0))
    val state: StateFlow<Telemetry> = _state
    fun publish(drawCalls:Int, fps:Int, msUpdate:Double, msRender:Double) {
        _state.value = Telemetry(fps, drawCalls, msUpdate, msRender)
    }
}
