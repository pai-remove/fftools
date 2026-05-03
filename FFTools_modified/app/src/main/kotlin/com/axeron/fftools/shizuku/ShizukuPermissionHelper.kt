// app/src/main/kotlin/com/axeron/fftools/shizuku/ShizukuPermissionHelper.kt
package com.axeron.fftools.shizuku

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku

object ShizukuPermissionHelper {

    private const val REQUEST_CODE = 1001

    // Callback results
    var onGranted: (() -> Unit)? = null
    var onDenied:  (() -> Unit)? = null

    // Listener yang di-register ke Shizuku
    private val requestListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == REQUEST_CODE) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    onGranted?.invoke()
                } else {
                    onDenied?.invoke()
                }
            }
        }

    fun register() {
        Shizuku.addRequestPermissionResultListener(requestListener)
    }

    fun unregister() {
        Shizuku.removeRequestPermissionResultListener(requestListener)
    }

    /**
     * Minta permission ke user via dialog Shizuku
     * Pastikan register() dipanggil sebelum ini
     */
    fun requestPermission() {
        if (ShizukuChecker.isGranted()) {
            onGranted?.invoke()
            return
        }
        try {
            Shizuku.requestPermission(REQUEST_CODE)
        } catch (e: Exception) {
            onDenied?.invoke()
        }
    }
}
