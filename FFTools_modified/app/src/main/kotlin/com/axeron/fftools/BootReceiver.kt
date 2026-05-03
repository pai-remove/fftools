// app/src/main/kotlin/com/axeron/fftools/BootReceiver.kt
package com.axeron.fftools

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.axeron.fftools.data.PreferencesManager
import com.axeron.fftools.service.ForegroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = PreferencesManager(context)
            if (prefs.autoStartService) {
                context.startForegroundService(
                    Intent(context, ForegroundService::class.java)
                )
            }
        }
    }
}
