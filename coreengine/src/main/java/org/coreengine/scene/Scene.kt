package org.coreengine.scene

import org.coreengine.camera.Camera
import org.coreengine.engine.CoreEngine
import org.coreengine.entity.Entity
import org.coreengine.hud.HudLayer
import org.coreengine.input.InputEvent
import org.coreengine.input.InputListener
import org.coreengine.render.Renderer
import org.coreengine.resource.ResourceManager
import org.coreengine.util.Debug



object EngineLocator {
    @Volatile lateinit var engine: CoreEngine
}
/**
 * Escena lógica y visual.
 *
 * Ciclo de vida:
 *  onCreate() → onUpdate(dt) → onDraw(renderer,camera) → onDestroy()
 *
 * Reglas:
 * - Las entidades del “mundo” viven en `children`.
 * - Las capas HUD viven en `_huds` y SIEMPRE se dibujan encima del mundo.
 * - El input se enruta HUD → Mundo (front-most primero).
 * - Sin referencias a android.*. La integración Android se inyecta desde el host.
 */
abstract class Scene : InputListener {

    companion object {
        /**
         * Proveedor opcional de HUD externo (p.ej., overlay Android).
         * El host (Android) puede asignarlo en tiempo de ejecución.
         * Si es null, la escena solo usa sus HUDs locales.
         */
        @JvmStatic
        var hudSupplier: (() -> HudLayer?)? = null
    }

    /** Cámara de la escena. Por defecto ortográfica sin tamaño inicial. */
    open val camera: Camera = Camera()

    val Scene.engine: CoreEngine get() = EngineLocator.engine

    // --- Mundo y HUDs propios de la escena ---
    private val children = mutableListOf<Entity>()
    private val _huds = mutableListOf<HudLayer>()

    /** Identificador único estable de la escena */
    open val id: String = this::class.simpleName ?: "UnnamedScene"

    /** Adjunta un nodo. Si es HudLayer, va a la pila HUD; si no, al mundo. */
    fun attachChild(e: Entity) { if (e is HudLayer) _huds += e else children += e }

    /** Desacopla un nodo. */
    fun detachChild(e: Entity) { if (e is HudLayer) _huds -= e else children -= e }

    /**
     * HUDs a dibujar por el motor. Incluye:
     * - HUD externo inyectado por el host (si existe).
     * - HUDs propios de la escena.
     */
    open fun provideHud(): List<HudLayer> = buildList {
        hudSupplier?.invoke()?.let { add(it) } // HUD externo (host)
        addAll(_huds)                           // HUDs locales
    }

    /** Hook de creación. No toca Android. */
    open fun onCreate() {
        Debug.i("crea la pantalla")
    }


    /**
     * Precarga de recursos. Se llama ANTES de onCreate().
     * Usa 'sceneId = this.toString()' para ownership.
     */
    open fun onCreateResources(resources: ResourceManager) {
        Debug.i("inicia los recursos")
    /* opcional */
    }

    /** Actualiza mundo y HUDs locales. */
    open fun onUpdate(delta: Float) {
        children.forEach { it.onUpdate(delta) }
        _huds.forEach { it.onUpdate(delta) }
    }

    /** Dibuja el mundo en back→front. El HUD se pinta fuera por HudManager. */
    open fun onDraw(renderer: Renderer, camera: Camera) {
        children.sortedBy { it.zIndex }.forEach { it.onDraw(renderer, camera) }
    }

    /** Limpieza propia. El host limpia overlays externos. */
    open fun onDestroy() {}

    // ----------------- Input -----------------

    /**
     * Enrutamiento de input:
     * 1) HUDs (front-most primero).
     * 2) Mundo (front-most primero).
     * Devuelve true si alguien consumió el evento.
     */
    override fun onInput(ev: InputEvent): Boolean {
        for (h in _huds.sortedByDescending { it.zIndex }) if (h.onInput(ev)) return true
        for (c in children.sortedByDescending { it.zIndex }) if (c.onInput(ev)) return true
        return false
    }

    // Persistencia de estao (snapshot/restore

    /** Devuelve estado serializable de la escena (por defecto vacío). */
    open fun saveState(): Map<String, Any?> = emptyMap()

    /** Restaura estado serializable (por defecto no-op). */
    open fun restoreState(state: Map<String, Any?>) {}
}
