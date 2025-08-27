package org.coreengine.time

interface Ticker {
    fun start(cb: TickCallback)
    fun stop()
    val isRunning: Boolean
}