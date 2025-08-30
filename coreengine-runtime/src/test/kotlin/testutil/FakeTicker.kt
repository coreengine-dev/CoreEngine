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

package testutil
import org.coreengine.api.time.Ticker

class FakeTicker : Ticker {
    private var cb: ((Float)->Unit)? = null
    override var isRunning: Boolean = false
        private set
    override fun start(loop: (Float) -> Unit) { isRunning = true; cb = loop }
    override fun stop() { isRunning = false; cb = null }
    fun tick(dt: Float) { cb?.invoke(dt) }
}
