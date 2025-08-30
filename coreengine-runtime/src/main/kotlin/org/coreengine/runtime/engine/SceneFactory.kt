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

package org.coreengine.runtime.engine

import org.coreengine.api.scene.Scene


/**
 * Fábrica de escenas para navegación y diferir construcción.
 * TCD: evita acoplar creación con ejecución de ∇/𝐌.
 *  * Se añade 'id' por defecto (no abstracto) para identificar la escena.
 */


/*fun interface SceneFactory {
    fun create(): Scene
    val id: String get() = this::class.java.name
}*/
fun SceneFactory.id(): String = this::class.java.simpleName

typealias SceneFactory = () -> Scene