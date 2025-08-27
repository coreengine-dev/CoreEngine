package org.coreengine.android.host

import android.content.Context
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import org.coreengine.engine.CoreEngine
import org.coreengine.engine.EngineController
import org.coreengine.input.Action
import org.coreengine.input.InputEvent
import org.coreengine.render.canvas.CanvasRenderer

/**
 * CoreSurfaceView — Host de renderizado basado en SurfaceView.
 *
 * En el flujo TCD del libro:
 *   - 𝚿 (conciencia/input): canaliza eventos táctiles hacia el motor (InputManager).
 *   - 𝐌 (manifestación visible): provee la superficie sobre la que el Renderer dibuja.
 * Esta clase es el “puente” Android ↔ motor  (integración host Android).
 */
class CoreSurfaceView(ctx: Context) : SurfaceView(ctx), SurfaceHolder.Callback {

    // Hacemos las refs privadas y nullables para evitar crashes por acceso temprano.
    private var engine: CoreEngine? = null
    private var controller: EngineController? = null

    init {
        // El motor engancha el canvas a través del holder; registramos callbacks del ciclo de vida.
        holder.addCallback(this)

        // Necesario para accesibilidad y para poder usar performClick() sin warnings.
        isClickable = true
        isFocusable = true
    }

    // ---------- Ciclo de vida de SurfaceHolder.Callback ----------

    override fun surfaceCreated(h: SurfaceHolder) {
        // Puede llegar ANTES de bind(engine). Si no hay engine aún, no hacemos nada.
        val eng = engine ?: return

        // Adjunta el canvas al renderer si es CanvasRenderer.
        (eng.renderer as? CanvasRenderer)?.attach(h)

        // Arranca el loop del motor si tenemos controller y aún no corre.
        controller?.start()
    }

    override fun surfaceChanged(h: SurfaceHolder, f: Int, w: Int, hgt: Int) {
        // Ajusta la cámara activa a las dimensiones reales de la Surface.
        engine?.sceneManager?.current?.camera?.apply {
            setSize(w.toFloat(), hgt.toFloat())
            setViewport(0, 0, w, hgt)
        }
    }

    override fun surfaceDestroyed(h: SurfaceHolder) {
        // Para el loop con join/interrupt limpio (EngineController ya lo gestiona).
        controller?.stop()
    }

    // ---------- Entrada táctil (𝚿) ----------

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> Action.DOWN
            MotionEvent.ACTION_MOVE -> Action.MOVE
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // Llama al click accesible y emite “UP”
                performClick()
                Action.UP
            }
            else -> return false
        }
        // Post seguro al bus de input del motor; será consumido en el loop.
        engine?.inputManager?.post(InputEvent.Touch(ev.x, ev.y, action))
        return true
    }

    // Requisito para quitar el warning de setOnTouchListener/performClick y habilitar accesibilidad.
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    // ---------- Acoplamiento al motor ----------

    /**
     * Debe llamarse ANTES de que la Surface esté operativa (idealmente al crear la vista).
     * Crea un controller dedicado al loop y deja todo listo para surfaceCreated().
     */
    fun bind(engine: CoreEngine) {
        this.engine = engine
        // Usa el factory del propio motor si existe; si no, construye el controller estándar.
        this.controller = engine.createController()
        // Si ya hay una surface válida (raro pero posible), ata y arranca.
        if (holder.surface?.isValid == true) {
            (engine.renderer as? CanvasRenderer)?.attach(holder)
            controller?.start()
        }
    }

    /**
     * Limpieza explícita (opcional). Útil si desmontas la vista fuera del ciclo de Surface.
     */
    fun shutdown() {
        controller?.stop()
        controller = null
        engine = null
    }
}
