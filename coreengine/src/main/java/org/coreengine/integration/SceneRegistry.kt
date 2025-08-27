// coreengine/integration/SceneRegistry.kt
package org.coreengine.integration

import org.coreengine.engine.SceneFactory
import java.util.concurrent.ConcurrentHashMap

object SceneRegistry {
    private val map = ConcurrentHashMap<String, SceneFactory>()

    fun register(id: String, factory: SceneFactory) { map[id] = factory }
    fun unregister(id: String) { map.remove(id) }
    fun get(id: String): SceneFactory? = map[id]
    fun ids(): Set<String> = map.keys
    fun asMap(): Map<String, SceneFactory> = map.toMap() // para restoreSnapshot
}
