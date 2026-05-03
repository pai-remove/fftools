// app/src/main/kotlin/com/axeron/fftools/features/DpiManager.kt
package com.axeron.fftools.features

import com.axeron.fftools.root.RootCommandExecutor
import com.axeron.fftools.shizuku.ShizukuServiceConnection

object DpiManager {

    // DPI target saat FF terbuka
    private const val FF_DPI = 240

    suspend fun enable() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.setDpi(FF_DPI)
        } else {
            RootCommandExecutor.exec("wm density $FF_DPI")
        }
    }

    suspend fun disable() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.resetDpi()
        } else {
            RootCommandExecutor.exec("wm density reset")
        }
    }

    suspend fun setCustomDpi(dpi: Int) {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.setDpi(dpi)
        } else {
            RootCommandExecutor.exec("wm density $dpi")
        }
    }
}
