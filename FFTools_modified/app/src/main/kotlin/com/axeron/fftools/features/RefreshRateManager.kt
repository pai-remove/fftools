// app/src/main/kotlin/com/axeron/fftools/features/RefreshRateManager.kt
package com.axeron.fftools.features

import com.axeron.fftools.root.RootCommandExecutor
import com.axeron.fftools.root.RootResult
import com.axeron.fftools.shizuku.ShizukuServiceConnection

object RefreshRateManager {

    suspend fun setMaxRefreshRate() {
        val maxRate = getDeviceMaxRate()
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.setRefreshRate(maxRate)
        } else {
            RootCommandExecutor.execMultiple(listOf(
                "settings put secure min_refresh_rate $maxRate",
                "settings put secure peak_refresh_rate $maxRate"
            ))
        }
    }

    suspend fun resetRefreshRate() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.writeSecureSetting("min_refresh_rate", "60")
            svc.writeSecureSetting("peak_refresh_rate", "60")
        } else {
            RootCommandExecutor.execMultiple(listOf(
                "settings put secure min_refresh_rate 60",
                "settings put secure peak_refresh_rate 60"
            ))
        }
    }

    private suspend fun getDeviceMaxRate(): Int {
        val svc = ShizukuServiceConnection.service
        return svc?.getMaxRefreshRate() ?: run {
            val result = RootCommandExecutor.exec(
                "dumpsys display | grep mRefreshRate | head -1"
            )
            if (result.isSuccess) {
                val match = Regex("mRefreshRate=(\\d+)").find(
                    (result as RootResult.Success).output
                )
                match?.groupValues?.get(1)?.toIntOrNull() ?: 60
            } else 60
        }
    }
}
