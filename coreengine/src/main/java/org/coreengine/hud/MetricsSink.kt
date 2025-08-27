package org.coreengine.hud

interface MetricsSink {
    fun onTimings(msUpdate: Float, msRender: Float, drawCalls: Int)
}