// :coreengine-android-host/src/main/java/.../BaseCoreEngineActivity.kt
package org.coreengine.android.host.base

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import org.coreengine.android.host.AndroidFontManager
import org.coreengine.android.host.AndroidTextureManager
import org.coreengine.android.host.CoreSurfaceHost
import org.coreengine.android.host.MainActivityHook
import org.coreengine.engine.CoreEngine
import org.coreengine.engine.SceneFactory
import org.coreengine.hud.HudLayer
import org.coreengine.render.canvas.MetricsOverlayCanvas
import org.coreengine.render.Renderer
import org.coreengine.resource.CanvasVbomProvider
import org.coreengine.resource.EngineServices
import org.coreengine.resource.ResourceManager

abstract class BaseCoreEngineActivity : ComponentActivity() {

    protected lateinit var engine: CoreEngine
    private lateinit var host: CoreSurfaceHost

    protected lateinit var hook: MainActivityHook
    // Overlay para métricas dibujadas en Canvas
    protected val overlay = MetricsOverlayCanvas()

    /** Renderer obligatorio (CanvasRenderer, GL, etc.) */
    protected abstract fun provideRenderer(): Renderer

    /** Registrar canales (e.g. "assets") ANTES de la primera escena */
    protected abstract fun registerResourceChannels(res: ResourceManager)

    /** Fábrica de escena inicial (o null si la setearás luego) */
    protected open fun initialScene(): SceneFactory? = null

    /** HUD opcional (ej. métricas) */
    protected open fun fpsHudFactory(): (() -> HudLayer)? = null

    /** La hija construye y devuelve el motor listo (renderer, resourceSetup, sceneFactory, etc.). */
    protected abstract fun buildEngine(): CoreEngine



    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Construir motor (ya registra canales en resourceSetup antes de la 1ª escena)
        engine = buildEngine()



        host = CoreSurfaceHost(this).also { it.bind(this, engine,application) }
        setContentView(host)




    }


    override fun onResume() {
        super.onResume() /* surfaceCreated arranca */

    }

    override fun onDestroy() {
        engine.stop()
        super.onDestroy()
    }
}
