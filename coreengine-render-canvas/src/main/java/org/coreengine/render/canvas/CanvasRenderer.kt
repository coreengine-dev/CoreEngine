package org.coreengine.render.canvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import org.coreengine.camera.Camera
import org.coreengine.camera.Origin
import org.coreengine.entity.Entity
import org.coreengine.render.Renderer

// org.coreengine.render.canvas.CanvasRenderer
class CanvasRenderer : Renderer {
    @Volatile private var holder: SurfaceHolder? = null

    // Canvas del frame actual (puede ser null si lockCanvas falla)
    private var canvas: Canvas? = null
    val hasCanvas: Boolean
        get() = canvas != null

    fun withCanvas(block: (Canvas) -> Unit) {
        val c = canvas ?: return
        block(c)
    }


    // Cámara activa SOLO cuando hay canvas válido
    private var currentCamera: Camera? = null

    // --- métrica ---
    private var draws = 0
    override val drawCallsThisFrame: Int get() = draws
    private inline fun markDraw(block: () -> Unit) { draws++; block() }

    fun attach(holder: SurfaceHolder) { this.holder = holder }

    override fun begin(camera: Camera) {
        // Reinicia contador por frame
        draws = 0

        // Intentar bloquear canvas; si falla, no hay frame este tick.
        val c = holder?.lockCanvas()
        if (c == null) {
            // No hay superficie lista: evita NPEs y deja el frame como “saltado”.
            canvas = null
            currentCamera = null
            return
        }

        canvas = c
        currentCamera = camera

        // Configurar transformaciones de viewport de forma segura (sin divisiones por cero).
        c.save()

        // Viewport en px
        c.translate(camera.viewportX.toFloat(), camera.viewportY.toFloat())

        // Escala mundo→viewport con zoom (protege width/height == 0)
        val safeW = if (camera.width  > 0f) camera.width  else 1f
        val safeH = if (camera.height > 0f) camera.height else 1f
        val sx = (camera.viewportW / safeW) * camera.zoom
        val sy = (camera.viewportH / safeH) * camera.zoom
        c.scale(sx, sy)

        // Origen vertical si BOTTOM_LEFT
        if (camera.origin == Origin.BOTTOM_LEFT) {
            c.translate(0f, safeH)
            c.scale(1f, -1f)
        }
    }

    override fun draw(entity: Entity) {
        val cam = currentCamera ?: return
        // No incrementamos aquí: los draw calls reales son los helpers (rect/text/circle).
        entity.onDraw(this, cam)
    }

    override fun clear(r: Float, g: Float, b: Float, a: Float) {
        canvas?.drawColor(
            Color.argb(
                (a * 255).toInt(),
                (r * 255).toInt(),
                (g * 255).toInt(),
                (b * 255).toInt()
            )
        )
    }

    override fun end() {
        // Si no hubo canvas, no hay nada que restaurar/postear.
        val c = canvas ?: return
        try {
            c.restore()
            holder?.unlockCanvasAndPost(c)
        } finally {
            canvas = null
            currentCamera = null // limpia referencias del frame
        }
    }

    // Helpers de dibujo SIN allocs y contando draw calls reales
    fun drawRect(x: Float, y: Float, w: Float, h: Float, paint: Paint) {
        val c = canvas ?: return
        markDraw { c.drawRect(x, y, x + w, y + h, paint) }
    }



    fun drawText(text: String, x: Float, y: Float, paint: Paint) {
        val c = canvas ?: return
        markDraw { c.drawText(text, x, y, paint) }
    }

    fun drawCircle(cx: Float, cy: Float, r: Float, paint: Paint) {
        val c = canvas ?: return
        markDraw { c.drawCircle(cx, cy, r, paint) }
    }

}

class BitmapSprite(private val bmp: Bitmap, var px: Float, var py: Float) : Entity() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(r: Renderer, c: Camera) {
        val cr = r as? CanvasRenderer ?: return
        cr.withCanvas { canvas ->
            canvas.drawBitmap(bmp, px, py, paint)
        }
    }
}
