package org.coreengine.resource

class CanvasVbomProvider : VbomProvider {
    private var nextId = 1
    override fun create(capacity: Int) = VertexBuffer(nextId++, capacity)
}