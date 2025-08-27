package org.coreengine.android.host

import android.view.Choreographer
import org.coreengine.time.TickCallback
import org.coreengine.time.Ticker

class AndroidVsyncTicker : Ticker, Choreographer.FrameCallback {
    private val ch = Choreographer.getInstance()
    private var last = 0L
    private var cb: TickCallback? = null
    @Volatile override var isRunning: Boolean = false
        private set

    override fun start(cb: TickCallback) {
        if (isRunning) return
        this.cb = cb
        isRunning = true
        last = 0L
        ch.postFrameCallback(this)
    }

    override fun doFrame(t: Long) {
        if (!isRunning) return
        if (last == 0L) last = t
        val dt = ((t - last) / 1_000_000_000.0).toFloat().coerceAtMost(0.1f)
        last = t
        cb?.onTick(dt)
        ch.postFrameCallback(this)
    }

    override fun stop() {
        if (!isRunning) return
        isRunning = false
        ch.removeFrameCallback(this)
        cb = null
    }
}
