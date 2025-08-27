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

package org.coreengine.engine

import org.coreengine.state.EngineSnapshot

/**
 * Hooks de snapshot/restore.
 * - Mantienen el motor limpio y preparados para ampliar a save-state de escenas.
 */

/*fun CoreEngine.takeSnapshot(): EngineSnapshot {
    val ui = store.uiState.value
    val sceneName = sceneManager.current?.toString() ?: "none"

    return EngineSnapshot(
        frame = ui.frame,
        sceneId = sceneName,
        counters = mapOf(
            "drawCalls" to ui.drawCalls,
            "heapKb" to ui.heapKb,
            "droppedFrames" to ui.droppedFrames
        ),
        // Por ahora solo “frameMs” si alguna vez lo añades; usa 0f como placeholder.
        timersMs = mapOf(
            "frameMs" to 0f
        )
    )
}*/

/**
 * Placeholder intencional.
 * Cuando tengas saveState()/restoreState en Scene + registry de factories,
 * implementa aquí la recreación de escena y aplicación de su estado.
 */
fun CoreEngine.restoreSnapshot(snapshot: EngineSnapshot, registry: Map<String, SceneFactory>) {
    // No-op de momento.
}
