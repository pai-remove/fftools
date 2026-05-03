// app/src/main/kotlin/com/axeron/fftools/shizuku/ShizukuChecker.kt
package com.axeron.fftools.shizuku

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku

object ShizukuChecker {

    /**
     * Shizuku app terinstall DAN servicenya udah jalan
     */
    fun isAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * App kita udah dapet permission dari Shizuku
     */
    fun isGranted(): Boolean {
        if (!isAvailable()) return false
        return try {
            // Kalau version < 11, pakai cara lama
            if (Shizuku.isPreV11()) {
                false
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cek apakah Shizuku jalan via ADB (non-root)
     * atau via root — untuk info ke user
     */
    fun isRunningViaAdb(): Boolean {
        return try {
            Shizuku.getUid() == 2000 // UID 2000 = ADB shell
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cek apakah Shizuku jalan via root
     */
    fun isRunningViaRoot(): Boolean {
        return try {
            Shizuku.getUid() == 0 // UID 0 = root
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Status lengkap untuk ditampilkan di UI
     */
    fun getStatus(): ShizukuStatus {
        return when {
            !isAvailable() -> ShizukuStatus.NOT_RUNNING
            !isGranted()   -> ShizukuStatus.NOT_GRANTED
            else           -> ShizukuStatus.READY
        }
    }
}

enum class ShizukuStatus {
    NOT_RUNNING,   // Shizuku belum jalan / tidak terinstall
    NOT_GRANTED,   // Jalan tapi belum dikasih izin
    READY          // Siap dipakai
}
