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

package org.coreengine

// src/test/java/org/coreengine/camera/CameraTest.kt

import org.coreengine.camera.Camera
import org.coreengine.camera.Origin
import kotlin.math.abs
import kotlin.test.Test

private fun assertFloatEq(a: Float, b: Float, eps: Float = 1e-4f) {
    assert(abs(a - b) <= eps) { "Expected $a ≈ $b" }
}

class CameraTest {

    @Test
    fun project_unproject_TOP_LEFT_identity() {
        val cam = Camera(800f, 600f, zoom = 1f, origin = Origin.TOP_LEFT).apply {
            setViewport(0, 0, 800, 600)
        }
        val pts = listOf(0f to 0f, 400f to 300f, 799f to 599f, 123f to 456f)
        for ((wx, wy) in pts) {
            val (sx, sy) = cam.project(wx, wy)
            val (wx2, wy2) = cam.unproject(sx, sy)
            assertFloatEq(wx, wx2); assertFloatEq(wy, wy2)
        }
    }

    @Test fun project_unproject_BOTTOM_LEFT_identity() {
        val cam = Camera(800f, 600f, zoom = 1f, origin = Origin.BOTTOM_LEFT).apply {
            setViewport(0, 0, 800, 600)
        }
        val pts = listOf(0f to 0f, 400f to 300f, 799f to 599f, 123f to 456f)
        for ((wx, wy) in pts) {
            val (sx, sy) = cam.project(wx, wy)
            val (wx2, wy2) = cam.unproject(sx, sy)
            assertFloatEq(wx, wx2); assertFloatEq(wy, wy2)
        }
    }

    @Test fun viewport_offset_and_scale() {
        val cam = Camera(100f, 50f, zoom = 1f, origin = Origin.TOP_LEFT).apply {
            setViewport(10, 20, 200, 100) // factor 2x en cada eje y offset
        }
        val (sx, sy) = cam.project(25f, 10f)
        assertFloatEq(sx, 10f + (25f / 100f) * 200f) // 10 + 50
        assertFloatEq(sy, 20f + (10f / 50f) * 100f)  // 20 + 20
        val (wx, wy) = cam.unproject(sx, sy)
        assertFloatEq(wx, 25f); assertFloatEq(wy, 10f)
    }

    @Test fun zoom_affects_mapping_symmetrically() {
        val cam = Camera(200f, 100f, zoom = 2f, origin = Origin.TOP_LEFT).apply {
            setViewport(0, 0, 200, 100)
        }
        val (sx, sy) = cam.project(50f, 25f)
        // Con zoom=2, 50→ (50*2/200)*200 = 100; 25→(25*2/100)*100 = 50
        assertFloatEq(sx, 100f); assertFloatEq(sy, 50f)
        val (wx, wy) = cam.unproject(sx, sy)
        assertFloatEq(wx, 50f); assertFloatEq(wy, 25f)
    }
}
