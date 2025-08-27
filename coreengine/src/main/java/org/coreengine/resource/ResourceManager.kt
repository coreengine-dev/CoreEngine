// coreengine/resource/ResourceManager.kt
package org.coreengine.resource

import kotlin.reflect.KClass
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.cast

class ManagedResource<T: Any>(
    val id: String,
    val value: T,
    private val closer: (T) -> Unit
) {
    private val refs = AtomicInteger(1)
    fun retain() = refs.incrementAndGet()
    fun release(): Boolean {
        val count = refs.decrementAndGet()
        return if (count == 0) { closer(value); true } else false
    }
    val refCount: Int get() = refs.get()
}

fun interface Loader<T: Any> { fun load(id: String): T }

private data class ChannelKey(val name: String, val type: KClass<*>)

class ResourceManager {

    // id → recurso gestionado
    private val map = ConcurrentHashMap<String, ManagedResource<*>>()

    // sceneId → set de ids poseídos
    private val sceneOwnership = ConcurrentHashMap<String, MutableSet<String>>()

    // (canal, tipo) → (loader, closer)
    private val channels = ConcurrentHashMap<ChannelKey, Pair<Loader<*>, (Any)->Unit>>()

    /** Registra un canal para un tipo concreto (p.ej. "assets" + Bitmap). */
    fun <T: Any> registerChannel(
        channel: String,
        type: KClass<T>,
        loader: Loader<T>,
        closer: (T) -> Unit
    ) {
        channels[ChannelKey(channel, type)] = loader to { v -> closer(type.cast(v)) }
    }

    /** Obtiene un recurso usando id “canal:ruta/archivo.ext”. */
    @Suppress("UNCHECKED_CAST")
    fun <T: Any> get(
        id: String,
        type: KClass<T>,
        sceneId: String? = null
    ): T {
        val existing = map[id] as ManagedResource<T>?
        if (existing != null) {
            existing.retain()
            if (sceneId != null) sceneOwnership.computeIfAbsent(sceneId){ mutableSetOf() }.add(id)
            return existing.value
        }

        val colon = id.indexOf(':')
        require(colon > 0) { "ID debe ser 'canal:ruta', recibido '$id'" }
        val channel = id.substring(0, colon)
        val path = id.substring(colon + 1)

        val entry = channels[ChannelKey(channel, type)]
            ?: error("Channel '$channel' not registered for this type (${type.simpleName})")

        val loader = entry.first as Loader<T>
        val closer = entry.second as (T)->Unit

        val value = loader.load(path) // OJO: el loader recibe SOLO la ruta
        val mr = ManagedResource(id, value, closer)
        val prev = map.putIfAbsent(id, mr) as ManagedResource<T>?
        val use = prev ?: mr

        if (sceneId != null) sceneOwnership.computeIfAbsent(sceneId){ mutableSetOf() }.add(id)
        return use.value
    }

    fun release(id: String) {
        map[id]?.let { if (it.release()) map.remove(id) }
    }

    fun releaseOwnedBy(sceneId: String) {
        val owned = sceneOwnership.remove(sceneId) ?: return
        owned.forEach { release(it) }
    }
}
