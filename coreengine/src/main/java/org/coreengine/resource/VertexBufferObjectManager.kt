package org.coreengine.resource

interface VertexBufferObjectManager {
    fun create(capacity: Int): VertexBuffer
    fun update(vbo: VertexBuffer, data: FloatArray, count: Int)
    fun destroy(vbo: VertexBuffer)
}