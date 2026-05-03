// app/src/main/kotlin/com/axeron/fftools/service/FreeFireDetector.kt
package com.axeron.fftools.service

import android.app.ActivityManager
import android.content.Context
import kotlinx.coroutines.*

class FreeFireDetector(private val context: Context) {

    companion object {
        // Package name FF — cek dua versi (global & garena)
        val FF_PACKAGES = setOf(
            "com.dts.freefireth",
            "com.garena.game.ffthai",
            "com.dts.freefiremax"
        )
    }

    var onFFOpened: (() -> Unit)? = null
    var onFFClosed: (() -> Unit)? = null

    private var isFFRunning = false
    private var monitorJob: Job? = null

    private val activityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    fun startMonitoring(scope: CoroutineScope) {
        monitorJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                val running = isFFInForeground()

                if (running && !isFFRunning) {
                    isFFRunning = true
                    withContext(Dispatchers.Main) { onFFOpened?.invoke() }
                } else if (!running && isFFRunning) {
                    isFFRunning = false
                    withContext(Dispatchers.Main) { onFFClosed?.invoke() }
                }

                delay(1500) // cek tiap 1.5 detik
            }
        }
    }

    fun stopMonitoring() {
        monitorJob?.cancel()
        monitorJob = null
    }

    @Suppress("DEPRECATION")
    private fun isFFInForeground(): Boolean {
        val tasks = activityManager.getRunningTasks(1)
        if (tasks.isNullOrEmpty()) return false
        val topPackage = tasks[0].topActivity?.packageName ?: return false
        return topPackage in FF_PACKAGES
    }

    fun getRunningFFPackage(): String? {
        @Suppress("DEPRECATION")
        val processes = activityManager.runningAppProcesses ?: return null
        return processes.firstOrNull { proc ->
            proc.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
            proc.pkgList.any { it in FF_PACKAGES }
        }?.pkgList?.firstOrNull { it in FF_PACKAGES }
    }
}
