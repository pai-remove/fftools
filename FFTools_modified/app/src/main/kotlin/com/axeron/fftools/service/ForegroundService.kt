// app/src/main/kotlin/com/axeron/fftools/service/ForegroundService.kt
package com.axeron.fftools.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.axeron.fftools.MainActivity
import com.axeron.fftools.R
import com.axeron.fftools.data.PreferencesManager
import com.axeron.fftools.features.*
import kotlinx.coroutines.*

class ForegroundService : Service() {

    companion object {
        const val CHANNEL_ID      = "fftools_service"
        const val NOTIFICATION_ID = 1
        const val ACTION_STOP     = "com.axeron.fftools.STOP_SERVICE"
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var detector: FreeFireDetector
    private lateinit var prefs: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        prefs    = PreferencesManager(this)
        detector = FreeFireDetector(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(false))
        setupDetector()
        detector.startMonitoring(serviceScope)
    }

    private fun setupDetector() {
        detector.onFFOpened = {
            updateNotification(true)
            serviceScope.launch { applyFFSettings() }
        }
        detector.onFFClosed = {
            updateNotification(false)
            serviceScope.launch { revertSettings() }
        }
    }

    private suspend fun applyFFSettings() {
        if (prefs.isDpiEnabled)          DpiManager.setCustomDpi(prefs.customDpi)
        if (prefs.isHeadtrickEnabled)    HeadtrickManager.enable(prefs.headtrickLevel)
        if (prefs.isStabilizerEnabled)   StabilizerManager.enable()
        if (prefs.isRefreshRateEnabled)  RefreshRateManager.setMaxRefreshRate()
        if (prefs.isTouchTrackerEnabled) TouchTrackerManager.enable(
            TouchTrackerManager.Level.values().getOrElse(prefs.touchLevel) { TouchTrackerManager.Level.NORMAL }
        )
        if (prefs.isAspectRatioEnabled)  AspectRatioManager.enable()
        if (prefs.isSmoothnessEnabled) {
            val level = SmoothnessManager.Level.values()
                .getOrElse(prefs.smoothnessLevel) { SmoothnessManager.Level.MEDIUM }
            SmoothnessManager.enable(level)
        }
    }

    private suspend fun revertSettings() {
        if (prefs.isDpiEnabled)          DpiManager.disable()
        if (prefs.isHeadtrickEnabled)    HeadtrickManager.disable()
        if (prefs.isRefreshRateEnabled)  RefreshRateManager.resetRefreshRate()
        if (prefs.isTouchTrackerEnabled) TouchTrackerManager.disable()
        if (prefs.isAspectRatioEnabled)  AspectRatioManager.disable()
        if (prefs.isSmoothnessEnabled)   SmoothnessManager.disable()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) stopSelf()
        return START_STICKY
    }

    override fun onDestroy() {
        detector.stopMonitoring()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ── Notification ──────────────────────────────────────────────

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Shell",                              // ← nama channel = "Shell" (muncul di notif)
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Smart DPI AI Service"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(isActive: Boolean): Notification {
        val status = if (isActive) "Running" else "Waiting"
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, ForegroundService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Smart DPI AI")
            .setContentText("Version: INSIGHT PATCH | Status: $status")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(openIntent)
            .addAction(0, "Stop", stopIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(isActive: Boolean) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(isActive))
    }
}
