package com.axeron.fftools.privileged

import com.axeron.fftools.shizuku.ShizukuServiceConnection

object DisplayModeController {
    suspend fun setDensity(dpi: Int) {
        ShizukuServiceConnection.service?.setDisplayDensity(dpi)
    }
    suspend fun resetDensity() {
        ShizukuServiceConnection.service?.resetDisplayDensity()
    }
    suspend fun setRefreshRate(rate: Int) {
        ShizukuServiceConnection.service?.setRefreshRate(rate)
    }
}
