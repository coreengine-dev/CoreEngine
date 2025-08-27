package org.coreengine.time

class ThreadTicker(private val clock: Clock) : Ticker {
    @Volatile private var thread: Thread? = null
    @Volatile override var isRunning: Boolean = false
        private set

    override fun start(cb: TickCallback) {
        if (isRunning) return
        isRunning = true
        thread = Thread({
            while (isRunning) {
                val dt = clock.tick()
                cb.onTick(dt)
                clock.sleepToNextFrame()
            }
        }, "CoreEngine-Loop").apply {
            isDaemon = true
            priority = Thread.NORM_PRIORITY
            start()
        }
    }

    override fun stop() {
        isRunning = false
        thread?.let { t -> t.join(2000); if (t.isAlive) t.interrupt() }
        thread = null
    }
}