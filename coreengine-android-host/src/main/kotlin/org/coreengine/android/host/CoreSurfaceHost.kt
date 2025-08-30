/*
 * Copyright 2025 Juan José Nicolini
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.coreengine.android.host


import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import org.coreengine.api.input.InputEvent
import org.coreengine.canvas.CanvasRenderer
import org.coreengine.integration.HostLocator
import org.coreengine.runtime.engine.CoreEngine
import org.coreengine.runtime.engine.EngineController
import org.coreengine.runtime.util.Debug


class CoreSurfaceHost(ctx: Context) : FrameLayout(ctx), SurfaceHolder.Callback {

    private var lastW = 0
    private var lastH = 0

    private val surface = SurfaceView(ctx)
    private val overlay = ForwardingOverlay(ctx)

    private val androidHudManager by lazy { AndroidHudManager(overlay) }
    fun hudManager(): AndroidHudManager = androidHudManager

    var engine: CoreEngine? = null
        private set
    var controller: EngineController? = null
        private set


    private var fpsView: FpsHudView? = null

    init {
        Debug.i("host.init")

        addView(surface, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(overlay, LayoutParams(MATCH_PARENT, MATCH_PARENT))

        surface.holder.addCallback(this)
        surface.setZOrderOnTop(false)
        surface.setZOrderMediaOverlay(false)

        overlay.visibility = VISIBLE
        overlay.bringToFront()
        overlay.elevation = 1f
        overlay.forwardTarget = surface

        surface.setOnTouchListener { _, ev ->
            val e = when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> InputEvent.Down(ev.x, ev.y, ev.getPointerId(0))
                MotionEvent.ACTION_MOVE -> InputEvent.Move(ev.x, ev.y, ev.getPointerId(0))
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    InputEvent.Up(ev.x, ev.y, ev.getPointerId(0))

                else -> return@setOnTouchListener false
            }
            engine?.inputManager?.post(e)
            if (ev.actionMasked == MotionEvent.ACTION_UP) surface.performClick()
            true
        }
        isClickable = true
        isFocusable = true
    }

    fun bind(activity: Activity, engine: CoreEngine, app: Application) {
        this.engine = engine
        Debug.i("bind: start")
        // Ticker VSYNC en host (core puro)
        controller = engine.createController(AndroidVsyncTicker())

        // Bridge + HUD Android
        HostLocator.host = AndroidHostBridge(activity, overlay)
        Debug.i("bind: host bridge set")
        installAndroidHudAuto()
        Debug.i("bind: hud auto installed")

        // crea HUD si no existe y si existe lo usa
        attachOrReattachFpsView(activity)

        Debug.i("bind: ensureFpsHud done")

        // enlaza muestras
        controller?.onSample = { s ->
            overlay.post { fpsView?.update(s) }    // UI thread
        }

        startIfReady()
        Debug.i("bind: end")
    }

    private fun attachOrReattachFpsView(activity: Activity) {
        overlay.post {
            if (fpsView == null) {
                Debug.i("FPS HUD -> crear (direct)")
                fpsView = FpsHudView(activity)
                overlay.addView(
                    fpsView,
                    LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.TOP or Gravity.START
                        leftMargin = 16; topMargin = 16
                    }
                )
            } else if (fpsView?.parent == null) {
                overlay.addView(fpsView)
            }
            overlay.bringToFront()
            overlay.elevation = 2f
            overlay.setBackgroundColor(0x3300FF00) // Fondo verde tenue
        }
    }

    private fun startIfReady() {
        val e = engine ?: return

        (e.renderer as? CanvasRenderer)?.attach(surface.holder)

        if (controller?.isRunning != true) {
            controller?.start()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Debug.i("surfaceCreated")
        startIfReady()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        lastW = width
        lastH = height
        engine?.sceneManager?.current?.camera?.apply {
            setSize(width.toFloat(), height.toFloat())
            setViewport(0, 0, width, height)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        controller?.stop()
        HostLocator.host = null
    }

    fun shutdown() {
        controller?.stop()
        controller = null
        engine = null
        HostLocator.host = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        shutdown()
    }


}



