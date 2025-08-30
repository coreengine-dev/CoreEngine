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

package render
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import testutil.FakeRenderer

class CanvasRendererTest {
    @Test fun beginWithoutHolderDoesNotCrash() {
        val r = FakeRenderer()
        r.begin(testCamera())
        r.end()
        assertEquals(0, r.drawCallsThisFrame)
    }

    @Test fun endWithoutCanvasSafe() {
        val r = FakeRenderer()
        r.end() // no crash
    }

    // helpers
    private fun testCamera() = object : org.coreengine.api.camera.Camera {
        override var rotation: Float = 0f
        override val width: Float = 100f
        override val height: Float = 100f
        override val xMin: Float = 0f
        override val yMin: Float = 0f
        override fun setSize(w: Float, h: Float) {}
        override fun setViewport(x: Int, y: Int, w: Int, h: Int) {}
        override fun worldToScreen(x: Float, y: Float) = x to y
        override fun screenToWorld(x: Float, y: Float) = x to y
    }
}
