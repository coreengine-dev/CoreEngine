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

// coreengine/integration/SceneRegistry.kt
package org.coreengine.integration

import java.util.concurrent.ConcurrentHashMap

/*object SceneRegistry {
    private val map = ConcurrentHashMap<String, SceneFactory>()

    fun register(id: String, factory: SceneFactory) { map[id] = factory }
    fun unregister(id: String) { map.remove(id) }
    fun get(id: String): SceneFactory? = map[id]
    fun ids(): Set<String> = map.keys
    fun asMap(): Map<String, SceneFactory> = map.toMap() // para restoreSnapshot
}*/
