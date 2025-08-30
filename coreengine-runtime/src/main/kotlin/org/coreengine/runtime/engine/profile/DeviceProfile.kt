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


// ===============================================
// MÓDULO: coreengine-monitor (dedicado a mejoras)
// Paquetes oficiales: org.coreengine.* (se mantienen)
// Este módulo NO cambia tus clases existentes. Las envuelve.
// Mapea TCD: 𝛂→𝜱→𝝮→𝐌→𝚿→𝝎 en comentarios y contratos.
// ===============================================

// ─────────────────────────────────────────────────
// Archivo: org/coreengine/engine/profile/DeviceProfile.kt
// Rol TCD: 𝛂 (tiempo, reloj base del sistema)
// ─────────────────────────────────────────────────
package api.coreengine.runtime.engine.profile

data class DeviceProfile(
    val name: String,
    val width: Int,
    val height: Int,
    val dpi: Int,
    val targetFps: Int,
    val heapBudgetMB: Int,
    val ioLatencyMs: Int,
    val gpuTier: Int
)

object Profiles {
    // Perfiles de referencia. Úsalos en desarrollo/CI.
    val LowPhone  = DeviceProfile("LowPhone", 720, 1520, 300, 30, 64, 8, 1)
    val MidPhone  = DeviceProfile("MidPhone", 1080, 2400, 420, 60, 128, 4, 2)
    val HighPhone = DeviceProfile("HighPhone", 1440, 3120, 560, 120, 256, 2, 3)
}

interface ProfileRunner {
    /** 𝛂: Ajusta reloj y límites globales del motor. */
    fun apply(profile: DeviceProfile)
}

















