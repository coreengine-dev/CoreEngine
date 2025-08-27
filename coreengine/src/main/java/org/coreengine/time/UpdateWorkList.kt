
// ─────────────────────────────────────────────────
// Archivo: org/coreengine/time/UpdateWorkList.kt
// Rol TCD: 𝛂→𝜱 (orden de actualización de sistemas/escenas)
// Planificador no intrusivo: BFS/DFS para tu update().
// ─────────────────────────────────────────────────
package org.coreengine.time

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
