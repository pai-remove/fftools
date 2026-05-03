// app/src/main/kotlin/com/axeron/fftools/features/StabilizerManager.kt
package com.axeron.fftools.features

import com.axeron.fftools.root.RootCommandExecutor
import com.axeron.fftools.shizuku.ShizukuServiceConnection

object StabilizerManager {

    /**
     * Kill background apps + trim memory agar FF dapat lebih banyak RAM
     */
    suspend fun enable() {
        val svc = ShizukuServiceConnection.service
        if (svc != null) {
            svc.killBackgroundApps()
        } else {
            RootCommandExecutor.execMultiple(listOf(
                // Kill proses background non-essential
                "am send-trim-memory all 80",
                // Drop cache (butuh root)
                "sync",
                "echo 3 > /proc/sys/vm/drop_caches",
                // Naikkan priority proses FF
                "renice -20 \$(pidof com.dts.freefireth)",
                "renice -20 \$(pidof com.garena.game.ffthai)"
            ))
        }
    }

    suspend fun disable() {
        // Tidak perlu undo — background apps akan kembali sendiri
    }
}
