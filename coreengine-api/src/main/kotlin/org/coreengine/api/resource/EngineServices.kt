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

import org.coreengine.api.render.Renderer
import org.coreengine.api.util.Logger


/*interface EngineServices {
    val renderer: Renderer
    val textures: TextureManager
    val fonts: FontManager
    val vbom: VbomProvider
    val logger: Logger
}*/

// :coreengine-api
interface EngineServices {
    val renderer: Renderer
    val resourceManager: ResourceManager
    val logger: Logger
    // accesos convenientes
    val textures: TextureManager? get() = resourceManager.textures
    val fonts: FontManager? get() = resourceManager.fonts
    val vbom: VbomProvider? get() = resourceManager.vbom
}
