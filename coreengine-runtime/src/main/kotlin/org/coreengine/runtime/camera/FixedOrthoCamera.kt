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

package org.coreengine.runtime.camera

import org.coreengine.api.camera.Camera

class FixedOrthoCamera : Camera {
    override var rotation: Float = 0f
    private var w = 0f; private var h = 0f
    override val width get() = w
    override val height get() = h
    override val xMin get() = 0f
    override val yMin get() = 0f

    override fun setSize(w: Float, h: Float) { this.w = w; this.h = h }
    override fun setViewport(x: Int, y: Int, w: Int, h: Int) { /* no-op */ }

    override fun worldToScreen(x: Float, y: Float) = x to y
    override fun screenToWorld(x: Float, y: Float) = x to y
}