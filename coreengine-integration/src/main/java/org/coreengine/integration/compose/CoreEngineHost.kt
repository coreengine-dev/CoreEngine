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



@file:Suppress("FunctionName")
package org.coreengine.integration.compose

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.coreengine.android.host.CoreSurfaceHost
import org.coreengine.engine.CoreEngine

@Composable
fun CoreEngineHost(
    engine: CoreEngine,
    modifier: Modifier = Modifier,
    overlay: @Composable (fps: Int) -> Unit = { Text("FPS $it") }
) {
    val ctx = LocalContext.current
    val activity = remember(ctx) { ctx.findActivity() ?: error("Activity requerida") }

    // Mantén una única instancia del host (sobrevive a recomposiciones)
    val host = remember { CoreSurfaceHost(activity) }

    // Vincular una vez por engine/host
    LaunchedEffect(engine, host) {
//        host.bind(activity, engine)
    }

    // Parar y limpiar al salir de la composición
    DisposableEffect(host) {
        onDispose { host.shutdown() }
    }

    val ui = engine.store.uiState.collectAsStateWithLifecycle()

    Box(modifier) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { host }
        )
        overlay(ui.value.fps) // se dibuja por encima del SurfaceView
    }


}

/* -------- helpers -------- */

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
