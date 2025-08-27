package org.coreengine.engine

import org.coreengine.time.*

class EngineController internal constructor(
    private val engine: CoreEngine,
    private val tickerFactory: (Clock) -> Ticker = { c -> ThreadTicker(c) } // default JVM
) {
    @Volatile private var ticker: Ticker? = null

    @Synchronized fun start() {
        if (ticker?.isRunning == true) return
        engine.start()
        val t = tickerFactory(engine.clock)
        t.start { dt -> engine.tickFrame(dt) }
        ticker = t
    }

    @Synchronized fun stop() {
        ticker?.stop()
        ticker = null
        engine.stop()
    }

    fun pause()  = engine.pause()
    fun resume() = engine.resume()
    val isAlive: Boolean get() = ticker?.isRunning == true
}
