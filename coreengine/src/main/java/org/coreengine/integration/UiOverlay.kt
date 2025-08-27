package org.coreengine.integration

interface UiOverlay {
    fun add(id: String, build: () -> Any)            // Any = View en host
    fun remove(id: String)
    fun clear()
}