package org.coreengine.resource

class CanvasVBOM : VertexBufferObjectManager {
    private var nextId = 1
    override fun create(capacity: Int) = VertexBuffer(nextId++, capacity)
    override fun update(vbo: VertexBuffer, data: FloatArray, count: Int) { /* no-op */ }
    override fun destroy(vbo: VertexBuffer) { /* no-op */ }
}