// app/src/main/kotlin/com/axeron/fftools/utils/DeviceInfo.kt
package com.axeron.fftools.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics

object DeviceInfo {

    fun getModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"

    fun getAndroidVersion(): String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    fun getScreenDensity(context: Context): Int {
        return context.resources.displayMetrics.densityDpi
    }

    fun getScreenResolution(context: Context): String {
        val dm: DisplayMetrics = context.resources.displayMetrics
        return "${dm.widthPixels}x${dm.heightPixels}"
    }

    fun getSummary(context: Context): String {
        return buildString {
            appendLine("Device  : ${getModel()}")
            appendLine("OS      : ${getAndroidVersion()}")
            appendLine("DPI     : ${getScreenDensity(context)}")
            appendLine("Resolusi: ${getScreenResolution(context)}")
        }
    }
}
