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

import android.app.Application
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CoreSurfaceHostTest {

    @Test
    fun hostBindsAndShowsHud() {
        ActivityScenario.launch(EmptyHostActivity::class.java).use { scenario ->
            scenario.onActivity { act ->
                val host = CoreSurfaceHost(act)
                act.setContentView(host, android.widget.FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

                val engine = DemoEngines.makeMinimalEngine()
                host.bind(act, engine, act.application as Application)

                // Inserta HUD directo para verificar overlay
                host.post {
                    val v = FpsHudView(act).apply { text = "HUD test" }
                    host.overlay.add(v)
                }
            }
        }
    }
}
