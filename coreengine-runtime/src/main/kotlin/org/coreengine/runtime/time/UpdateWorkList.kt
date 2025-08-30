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


// ─────────────────────────────────────────────────
// Archivo: org/coreengine/time/UpdateWorkList.kt
// Rol TCD: 𝛂→𝜱 (orden de actualización de sistemas/escenas)
// Planificador no intrusivo: BFS/DFS para tu update().
// ─────────────────────────────────────────────────
package org.coreengine.runtime.time

enum class Strategy { BFS, DFS }

data class UpdateNode(val id: String, val priority: Int = 0, val update: (Float) -> Unit)

class UpdateWorkList(private val strategy: Strategy = Strategy.BFS) {
    private val dq: ArrayDeque<UpdateNode> = ArrayDeque()

    fun push(n: UpdateNode) {
        if (strategy == Strategy.BFS) dq.add(n)
        else dq.add(0, n) // inserta al inicio
    }

    fun pop(): UpdateNode? {
        return if (dq.isEmpty()) null else dq.removeAt(0)
    }


    fun isEmpty() = dq.isEmpty()

    fun clear() = dq.clear()
}
