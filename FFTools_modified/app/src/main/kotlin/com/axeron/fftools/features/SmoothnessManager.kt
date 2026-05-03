// app/src/main/kotlin/com/axeron/fftools/features/SmoothnessManager.kt
package com.axeron.fftools.features

import com.axeron.fftools.root.RootCommandExecutor
import com.axeron.fftools.shizuku.ShizukuServiceConnection

object SmoothnessManager {

    enum class Level {
        LOW,    // Sedikit licin — natural, aman untuk semua HP
        MEDIUM, // 2x lebih licin dari default
        HIGH    // Maksimal licin — terbaik untuk FF
    }

    /**
     * Kombinasi pointer speed + touch dispatch latency.
     * Pointer speed: -7 s/d +7 (Android default = 0, makin negatif makin cepat/licin)
     * touch_slop & long_press_timeout dipakai sebagai pelengkap.
     */
    suspend fun enable(level: Level) {
        val svc = ShizukuServiceConnection.service
        val (pointerSpeed, touchSlop, longPress) = when (level) {
            Level.LOW    -> Triple("-2", "6", "400")
            Level.MEDIUM -> Triple("-4", "4", "300")
            Level.HIGH   -> Triple("-7", "2", "200")
        }
        if (svc != null) {
            // Gunakan writeSystemSetting & writeSecureSetting yang sudah ada di AIDL
            svc.writeSystemSetting("pointer_speed", pointerSpeed)
            svc.writeSystemSetting("touch_slop", touchSlop)
            svc.writeSecureSetting("long_press_timeout", longPress)
            svc.writeSystemSetting("input_dispatching_timeout", "3000")
        } else {
            RootCommandExecutor.execMultiple(
                listOf(
                    "settings put system pointer_speed $pointerSpeed",
                    "settings put system touch_slop $touchSlop",
                    "settings put secure long_press_timeout $longPress",
                    "settings put global input_dispatching_timeout 3000"
                )
            )
        }
    }

    suspend fun disable() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.writeSystemSetting("pointer_speed", "0")
            svc.writeSystemSetting("touch_slop", "8")
            svc.writeSecureSetting("long_press_timeout", "500")
            svc.writeSystemSetting("input_dispatching_timeout", "5000")
        } else {
            RootCommandExecutor.execMultiple(
                listOf(
                    "settings put system pointer_speed 0",
                    "settings put system touch_slop 8",
                    "settings put secure long_press_timeout 500",
                    "settings put global input_dispatching_timeout 5000"
                )
            )
        }
    }
}
