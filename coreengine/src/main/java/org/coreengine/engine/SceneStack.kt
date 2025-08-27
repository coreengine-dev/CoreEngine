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

package org.coreengine.engine

import org.coreengine.resource.ResourceManager
import org.coreengine.scene.Scene

/**
 * Pila de escenas: push/pop/replace.
 * Garantiza onCreate/onDestroy en el orden correcto.
 * TCD: organiza estados estructurales entre ticks.
 */

class SceneStack(private val resources: ResourceManager) {

    interface Listener { fun onSceneChanged(prev: Scene?, next: Scene) }

    private val listeners = mutableListOf<Listener>()
    fun addListener(l: Listener) { listeners += l }
    fun removeListener(l: Listener) { listeners -= l }

    private val stack = ArrayDeque<Scene>()
    val current: Scene? get() = stack.lastOrNull()


    private var _currentFactoryId: String? = null
    // Identificador de la fábrica de la escena actual.
    val currentFactoryId: String? get() = _currentFactoryId


    fun setScene(scene: Scene) {
        val prev = current
        //  libera recursos “owned” por la escena saliente
        prev?.let { resources.releaseOwnedBy(it.id) }
        prev?.onDestroy()
        stack.clear()
        // precarga ANTES de onCreate()
        scene.onCreateResources(resources)
        stack.add(scene)
        scene.onCreate()
        listeners.forEach { it.onSceneChanged(prev, scene) }

    }


    fun replace(factory: SceneFactory): Scene {
        val prev = current
        prev?.let { resources.releaseOwnedBy(it.id) }
        prev?.onDestroy()

        val next = factory.create()
        stack.clear()
        stack.add(next)
        next.onCreateResources(resources)
        next.onCreate()

        _currentFactoryId = factory.id
        listeners.forEach { it.onSceneChanged(prev, next) }
        return next
    }


    fun push(factory: SceneFactory): Scene {
        val next = factory.create()
        stack.add(next)
        next.onCreateResources(resources)
        next.onCreate()
        listeners.forEach { it.onSceneChanged(stack.dropLast(1).lastOrNull(), next) }
        return next
    }

    fun pop(): Boolean {
        if (stack.size <= 1) return false
        val removed = stack.removeLast()
        removed.onDestroy()
        resources.releaseOwnedBy(removed.id)
        current?.let { cur -> listeners.forEach { it.onSceneChanged(removed, cur) } }
        return true
    }

    // Alias
    fun getCurrentScene(): Scene? = current
}
