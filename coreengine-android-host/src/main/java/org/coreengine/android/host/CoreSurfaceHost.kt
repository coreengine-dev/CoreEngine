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
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import org.coreengine.engine.CoreEngine
import org.coreengine.engine.EngineController
import org.coreengine.input.Action
import org.coreengine.input.InputEvent
import org.coreengine.integration.HostLocator
import org.coreengine.render.canvas.CanvasRenderer
import org.coreengine.resource.CanvasVbomProvider
import org.coreengine.resource.EngineServices

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

    lateinit var services: EngineServices

    init {
        addView(surface, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        addView(overlay, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        surface.holder.addCallback(this)

        surface.setZOrderOnTop(false)
        surface.setZOrderMediaOverlay(false)
        overlay.bringToFront()
        overlay.elevation = 1f

        overlay.forwardTarget = surface

        surface.setOnTouchListener { _, ev ->
            val action = when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> Action.DOWN
                MotionEvent.ACTION_MOVE -> Action.MOVE
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> Action.UP
                else -> return@setOnTouchListener false
            }
            engine?.inputManager?.post(InputEvent.Touch(ev.x, ev.y, action))
            if (action == Action.UP) surface.performClick()
            true
        }
    }

    // CoreSurfaceHost.kt

    fun bind(activity: Activity, engine: CoreEngine, app: Application) {
        this.engine = engine

        // Inyecta VSYNC en el controller (core sigue puro)
        this.controller = engine.createController { _ -> AndroidVsyncTicker() }

        services = EngineServices(
            engine = engine,
            vbom = CanvasVbomProvider(),
            fontManager = AndroidFontManager(app),
            textureManager = AndroidTextureManager()
        )

        controller?.start()

        HostLocator.host = AndroidHostBridge(activity, overlay)
        installAndroidHudAuto()

        engine.sceneManager.addListener(object : org.coreengine.engine.SceneStack.Listener {
            override fun onSceneChanged(prev: org.coreengine.scene.Scene?, next: org.coreengine.scene.Scene) {
                hudManager().clear()
                if (lastW > 0 && lastH > 0) {
                    next.camera.setSize(lastW.toFloat(), lastH.toFloat())
                    next.camera.setViewport(0, 0, lastW, lastH)
                }
            }
        })

        startIfReady()
    }

    private fun startIfReady() {
        val e = engine ?: return
        val h = surface.holder
        (e.renderer as? CanvasRenderer)?.attach(h)
        controller?.start()

        e.sceneManager.current?.camera?.apply {
            if (lastW > 0 && lastH > 0) {
                setSize(lastW.toFloat(), lastH.toFloat())
                setViewport(0, 0, lastW, lastH)
            }
        }
    }



    override fun surfaceCreated(h: SurfaceHolder) { startIfReady() }

    override fun surfaceChanged(h: SurfaceHolder, f: Int, w: Int, hgt: Int) {
        lastW = w; lastH = hgt
        engine?.sceneManager?.current?.camera?.apply {
            setSize(w.toFloat(), hgt.toFloat())
            setViewport(0, 0, w, hgt)
        }
    }

    override fun surfaceDestroyed(h: SurfaceHolder) {
        controller?.stop()
        HostLocator.host = null
    }

    fun shutdown() {
        controller?.stop()
        controller = null
        engine = null
        HostLocator.host = null
    }
}

/** Reenvía eventos al Surface si los hijos del overlay no los consumen. */
class ForwardingOverlay(ctx: Context) : FrameLayout(ctx) {
    var forwardTarget: View? = null
    override fun onInterceptTouchEvent(ev: MotionEvent) = false
    override fun onTouchEvent(ev: MotionEvent) = false
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val handledByChildren = super.dispatchTouchEvent(ev)
        if (handledByChildren) return true
        return forwardTarget?.dispatchTouchEvent(ev) ?: false
    }
}
