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
package org.coreengine.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.coreengine.android.host.CoreSurfaceHost
import org.coreengine.canvas.CanvasRenderer
import org.coreengine.runtime.engine.CoreEngine
import org.coreengine.runtime.util.Debug

class MainActivity : AppCompatActivity() {
    private lateinit var host: CoreSurfaceHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Debug.i("MainActivity.onCreate")

        // 1) Crea y muestra el host primero
        host = CoreSurfaceHost(this)
        setContentView(host)


        // 2) Construye el engine con CanvasRenderer
        val engine = CoreEngine.Builder().apply {
            renderer = CanvasRenderer()
            sceneFactory = { SimpleScene() }
        }.build()
        Debug.i("engine construido")

        // 3) Vincula el engine al host (adjunta Surface y arranca controller VSYNC)
        host.bind(this, engine, application)
        Debug.i("host.bind hecho")
    }

    override fun onDestroy() {
        if (this::host.isInitialized) host.shutdown()
        super.onDestroy()
    }
}

