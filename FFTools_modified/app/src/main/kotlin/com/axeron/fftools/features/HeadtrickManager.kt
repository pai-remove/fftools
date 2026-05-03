// app/src/main/kotlin/com/axeron/fftools/features/HeadtrickManager.kt
package com.axeron.fftools.features

import com.axeron.fftools.root.RootCommandExecutor
import com.axeron.fftools.shizuku.ShizukuServiceConnection

object HeadtrickManager {

    /**
     * Headtrick bekerja dengan menurunkan pointer speed & mengurangi
     * touch slop agar aim lebih smooth & presisi (crosshair stabil)
     */
    suspend fun enable(level: Int = 1) {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.setTouchSensitivity(level)
        } else {
            val (speed, slop) = when (level) {
                2    -> Pair("-3", "4")   // extreme
                1    -> Pair("-2", "6")   // medium (default headtrick)
                else -> Pair("0",  "8")   // normal
            }
            RootCommandExecutor.execMultiple(listOf(
                "settings put system pointer_speed $speed",
                "settings put secure touch_slop $slop"
            ))
        }
    }

    suspend fun disable() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.setTouchSensitivity(0)
        } else {
            RootCommandExecutor.execMultiple(listOf(
                "settings put system pointer_speed 0",
                "settings put secure touch_slop 8"
            ))
        }
    }
}
