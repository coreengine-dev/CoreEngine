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

package org.coreengine.runtime.engine


/**
 * Pila de escenas: push/pop/replace.
 * Garantiza onCreate/onDestroy en el orden correcto.
 * TCD: organiza estados estructurales entre ticks.
 */


import org.coreengine.api.resource.EngineServices
import org.coreengine.api.scene.Scene


class SceneStack(
    private var services: EngineServices? = null
) {
    interface Listener { fun onSceneChanged(prev: Scene?, next: Scene) }

    private val listeners = mutableListOf<Listener>()
    private val backstack = ArrayDeque<Entry>()
    private var switching = false

    var current: Scene? = null
        private set

    var currentFactoryId: String? = null
        private set

    fun setServices(s: EngineServices) { services = s }

    fun set(factory: SceneFactory, factoryId: String? = null) {
        if (switching) return
        switching = true
        try {
            val next = factory()
            val id = factoryId ?: next.id.ifEmpty { next::class.java.name }

            // activar inicial
            current = next
            currentFactoryId = id
            safeCreateResources(next)
            safeCreate(next)
            notifyChange(null, next)
        } finally { switching = false }
    }

    fun replace(factory: SceneFactory, factoryId: String? = null) {
        if (switching) return
        switching = true
        try {
            val prev = current
            val next = factory()
            val id = factoryId ?: next.id.ifEmpty { next::class.java.name }

            // destruir anterior
            safeDestroy(prev)

            // activar nueva
            current = next
            currentFactoryId = id
            safeCreateResources(next)
            safeCreate(next)
            notifyChange(prev, next)
        } finally { switching = false }
    }

    fun push(factory: SceneFactory, factoryId: String? = null) {
        if (switching) return
        switching = true
        try {
            current?.let { backstack.addLast(Entry(it, currentFactoryId)) }

            val next = factory()
            val id = factoryId ?: next.id.ifEmpty { next::class.java.name }

            current = next
            currentFactoryId = id
            safeCreateResources(next)
            safeCreate(next)
            notifyChange(backstack.lastOrNull()?.scene, next)
        } finally { switching = false }
    }

    /** Vuelve a la escena anterior. Devuelve true si hubo pop. */
    fun pop(): Boolean {
        if (switching || backstack.isEmpty()) return false
        switching = true
        try {
            val prev = current
            val (restore, id) = backstack.removeLast()

            // destruir la actual
            safeDestroy(prev)

            // restaurar la anterior (ya creada previamente)
            current = restore
            currentFactoryId = id
            notifyChange(prev, restore)
            return true
        } finally { switching = false }
    }

    fun clear() {
        val prev = current
        current = null
        currentFactoryId = null
        backstack.asReversed().forEach { safeDestroy(it.scene) }
        backstack.clear()
        safeDestroy(prev)
        if (prev != null) notifyChange(prev, prev) // señal de limpieza
    }

    fun addListener(l: Listener) { listeners += l }
    fun removeListener(l: Listener) { listeners -= l }

    val backstackSize: Int get() = backstack.size

    // ---- helpers ----
    private data class Entry(val scene: Scene, val id: String?)

    private fun safeCreateResources(s: Scene?) {
        val sv = services ?: return
        try { s?.onCreateResources(sv) } catch (_: Throwable) {}
    }
    private fun safeCreate(s: Scene?) {
        try { s?.onCreate() } catch (_: Throwable) {}
    }
    private fun safeDestroy(s: Scene?) {
        try { s?.onDestroy() } catch (_: Throwable) {}
    }
    private fun notifyChange(prev: Scene?, next: Scene) {
        listeners.toList().forEach { l ->
            try { l.onSceneChanged(prev, next) } catch (_: Throwable) {}
        }
    }
}

