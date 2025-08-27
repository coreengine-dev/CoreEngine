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