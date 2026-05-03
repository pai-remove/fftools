// app/src/main/kotlin/com/axeron/fftools/features/TouchTrackerManager.kt
package com.axeron.fftools.features

import com.axeron.fftools.root.RootCommandExecutor
import com.axeron.fftools.shizuku.ShizukuServiceConnection

object TouchTrackerManager {

    enum class Level { NORMAL, HIGH, EXTREME }

    suspend fun enable(level: Level = Level.EXTREME) {
        val lvl = level.ordinal  // 0, 1, 2
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.setTouchSensitivity(lvl)
        } else {
            val pressure = when (level) {
                Level.EXTREME -> "2000"
                Level.HIGH    -> "1500"
                Level.NORMAL  -> "1000"
            }
            RootCommandExecutor.execMultiple(listOf(
                "settings put system touch_sensitivity $pressure",
                "settings put secure long_press_timeout 300"
            ))
        }
    }

    suspend fun disable() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.setTouchSensitivity(0)
        } else {
            RootCommandExecutor.execMultiple(listOf(
                "settings put system touch_sensitivity 1000",
                "settings put secure long_press_timeout 500"
            ))
        }
    }
}
