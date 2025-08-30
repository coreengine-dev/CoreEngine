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

package org.coreengine.api.camera

/**
 * Cámara ortográfica.
 * - Origen configurable: TOP_LEFT (Android) o BOTTOM_LEFT (GL clásico).
 * - project/unproject: mundo ↔ pantalla con zoom y viewport.
 */



interface Camera {
    var rotation: Float
    val width: Float
    val height: Float
    val xMin: Float
    val yMin: Float

    fun setSize(w: Float, h: Float)
    fun setViewport(x: Int, y: Int, w: Int, h: Int)
    fun worldToScreen(x: Float, y: Float): Pair<Float, Float>
    fun screenToWorld(x: Float, y: Float): Pair<Float, Float>
}


