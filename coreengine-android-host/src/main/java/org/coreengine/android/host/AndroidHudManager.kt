package org.coreengine.android.host

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import java.util.UUID

class AndroidHudManager(
    private val overlay: FrameLayout,
    private val main: Handler = Handler(Looper.getMainLooper())
) {
    data class Handle(val id: String)

    private val map = mutableMapOf<String, View>()

    fun add(
        build: (Context) -> View,
        lp: LayoutParams = defaultLP(),
        id: String? = null,
        onReady: ((View) -> Unit)? = null
    ): Handle {
        val key = id ?: UUID.randomUUID().toString()
        main.post {
            val ctx = overlay.context
            val v = build(ctx)
            removeById(key)               // idempotente
            overlay.addView(v, lp)
            map[key] = v
            onReady?.invoke(v)
        }
        return Handle(key)                // sin crear View
    }

    fun removeByHandle(h: Handle) { removeById(h.id) }

    fun removeById(id: String) {
        main.post {
            map.remove(id)?.let { overlay.removeView(it) }
        }
    }

    fun clear() {
        main.post {
            overlay.removeAllViews()
            map.clear()
        }
    }

    fun defaultLP() = LayoutParams(
        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
    )
}
