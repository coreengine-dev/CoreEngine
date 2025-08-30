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

package org.coreengine.api.resource

import kotlin.reflect.KClass
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.cast



/** Carga un recurso de una ruta lógica dentro de un canal. */
data class ChannelKey(val channel: String, val type: KClass<*>)

fun interface Loader<T> { fun load(path: String): T }

private class ManagedResource<T>(
    val id: String,
    val value: T,
    private val closer: (T) -> Unit
) {
    private val refs = AtomicInteger(1)

    fun retain() = refs.incrementAndGet()

    /** @return true si se cerró y debe borrarse del mapa */
    fun release(): Boolean {
        val n = refs.decrementAndGet()
        if (n == 0) {
            try { closer(value) } catch (_: Throwable) {}
            return true
        }
        return false
    }
}


class ResourceManager(
    // FACHADAS (opción A). Inyéctalas desde runtime/host.
    val textures: TextureManager? = null,
    val fonts: FontManager? = null,
    val vbom: VbomProvider? = null,
)
{

    // id → recurso gestionado
    private val map = ConcurrentHashMap<String, ManagedResource<*>>()

    // sceneId → set de ids poseídos
    private val sceneOwnership = ConcurrentHashMap<String, MutableSet<String>>()

    // (canal, tipo) → (loader, closer)
    private data class ChannelEntry(
        val load: (String) -> Any,
        val close: (Any) -> Unit
    )
    private val channels = ConcurrentHashMap<ChannelKey, ChannelEntry>()
    /** Registra un canal para un tipo concreto (p.ej. "assets" + Bitmap). */


    fun <T: Any> registerChannel(
        channel: String,
        type: KClass<T>,
        loader: Loader<T>,
        closer: (T) -> Unit
    ) {
        val loadAny: (String) -> Any = { path -> loader.load(path) }          // T -> Any
        val closeAny: (Any) -> Unit = { v -> closer(type.cast(v)) }           // Any -> T seguro
        channels[ChannelKey(channel, type)] = ChannelEntry(loadAny, closeAny)
    }

    inline fun <reified T: Any> registerChannel(
        channel: String,
        noinline loader: (String) -> T,
        noinline closer: (T) -> Unit
    ) = registerChannel(channel, T::class, Loader(loader), closer)





    /** Obtiene un recurso usando id “canal:ruta/archivo.ext”. */
    @Suppress("UNCHECKED_CAST")
    fun <T: Any> get(id: String, type: KClass<T>, sceneId: String? = null): T {
        (map[id] as ManagedResource<T>?)?.let { mr ->
            mr.retain(); if (sceneId != null) own(sceneId, id); return mr.value
        }

        val i = id.indexOf(':'); require(i > 0) { "ID debe ser 'canal:ruta', recibido '$id'" }
        val key = ChannelKey(id.substring(0, i), type)
        val entry = channels[key] ?: error("Channel '${key.channel}' no registrado para ${type.simpleName}")

        val valueT: T = type.cast(entry.load(id.substring(i + 1)))      // Any -> T seguro
        val mr = ManagedResource(id, valueT) { v: T -> entry.close(v) } // T -> Any implícito
        val prev = map.putIfAbsent(id, mr) as ManagedResource<T>?
        val use = prev ?: mr

        if (sceneId != null) own(sceneId, id)
        return use.value
    }

    /** Atajo reificado. */
    inline fun <reified T: Any> get(id: String, sceneId: String? = null): T =
        get(id, T::class, sceneId)


    /** Adopta un recurso ya creado bajo un id. Útil en tests o cachés. */
    fun <T: Any> adopt(id: String, value: T, closer: (T) -> Unit, sceneId: String? = null) {
        val mr = ManagedResource(id, value, closer)   // sin casts
        val prev = map.putIfAbsent(id, mr)
        require(prev == null) { "Recurso ya existente: $id" }
        if (sceneId != null) own(sceneId, id)
    }


    fun release(id: String) {
        map[id]?.let { if (it.release()) map.remove(id) }
    }

    fun releaseOwnedBy(sceneId: String) {
        val owned = sceneOwnership.remove(sceneId) ?: return
        owned.forEach { release(it) }
    }

    /** Limpia todo. Uso controlado en tests. */
    fun clearAll() {
        val ids = map.keys.toList()
        ids.forEach { release(it) }
        sceneOwnership.clear()
    }

    fun contains(id: String) = map.containsKey(id)

    // ---- helpers ----
    private fun own(sceneId: String, id: String) {
        sceneOwnership.getOrPut(sceneId) { mutableSetOf() }.add(id)
    }
}
