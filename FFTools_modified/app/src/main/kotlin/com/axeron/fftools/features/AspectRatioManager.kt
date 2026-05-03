// app/src/main/kotlin/com/axeron/fftools/features/AspectRatioManager.kt
package com.axeron.fftools.features

import com.axeron.fftools.root.RootCommandExecutor
import com.axeron.fftools.shizuku.ShizukuServiceConnection

object AspectRatioManager {

    /**
     * Optimasi density agar UI FF terlihat lebih lebar / FOV lebih luas
     */
    suspend fun enable(targetDpi: Int = 400) {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.setDisplayDensity(targetDpi)
        } else {
            RootCommandExecutor.exec("wm density $targetDpi")
        }
    }

    suspend fun disable() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.resetDisplayDensity()
        } else {
            RootCommandExecutor.exec("wm density reset")
        }
    }

    /**
     * Set size layar (override resolusi — butuh root/Shizuku)
     */
    suspend fun setDisplaySize(width: Int, height: Int) {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.writeSecureSetting("display_size_forced", "${width}x${height}")
        } else {
            RootCommandExecutor.exec("wm size ${width}x${height}")
        }
    }

    suspend fun resetDisplaySize() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.writeSecureSetting("display_size_forced", "")
        } else {
            RootCommandExecutor.exec("wm size reset")
        }
    }
}
