
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
package org.coreengine.engine.profile

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

















