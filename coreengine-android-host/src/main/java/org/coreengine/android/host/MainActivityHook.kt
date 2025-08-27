package org.coreengine.android.host

import org.coreengine.engine.EngineBootstrap
import org.coreengine.engine.profile.Profiles
import org.coreengine.render.canvas.MetricsOverlayCanvas

class MainActivityHook(
    private val overlay: MetricsOverlayCanvas,
    private val sceneIdProvider: () -> String,
    private val renderFunc: () -> Int
) {
    val bootstrap = EngineBootstrap(
        profile = Profiles.MidPhone,
        sceneIdProvider = sceneIdProvider,
        renderFunc = renderFunc
    ).apply {
        setOnSample { m -> overlay.update(m) }
    }

    fun onFrame(dt: Float) = bootstrap.tick(dt)
}