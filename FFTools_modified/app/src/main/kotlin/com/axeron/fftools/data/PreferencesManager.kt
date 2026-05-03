// app/src/main/kotlin/com/axeron/fftools/data/PreferencesManager.kt
package com.axeron.fftools.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fftools_prefs", Context.MODE_PRIVATE)

    var isDpiEnabled: Boolean
        get() = prefs.getBoolean("dpi_enabled", false)
        set(value) = prefs.edit().putBoolean("dpi_enabled", value).apply()

    var isHeadtrickEnabled: Boolean
        get() = prefs.getBoolean("headtrick_enabled", false)
        set(value) = prefs.edit().putBoolean("headtrick_enabled", value).apply()

    var isStabilizerEnabled: Boolean
        get() = prefs.getBoolean("stabilizer_enabled", false)
        set(value) = prefs.edit().putBoolean("stabilizer_enabled", value).apply()

    var isRefreshRateEnabled: Boolean
        get() = prefs.getBoolean("refresh_rate_enabled", false)
        set(value) = prefs.edit().putBoolean("refresh_rate_enabled", value).apply()

    var isTouchTrackerEnabled: Boolean
        get() = prefs.getBoolean("touch_tracker_enabled", false)
        set(value) = prefs.edit().putBoolean("touch_tracker_enabled", value).apply()

    var isAspectRatioEnabled: Boolean
        get() = prefs.getBoolean("aspect_ratio_enabled", false)
        set(value) = prefs.edit().putBoolean("aspect_ratio_enabled", value).apply()

    // Level settings
    var headtrickLevel: Int
        get() = prefs.getInt("headtrick_level", 1)
        set(value) = prefs.edit().putInt("headtrick_level", value).apply()

    var touchLevel: Int
        get() = prefs.getInt("touch_level", 2)
        set(value) = prefs.edit().putInt("touch_level", value).apply()

    var customDpi: Int
        get() = prefs.getInt("custom_dpi", 240)
        set(value) = prefs.edit().putInt("custom_dpi", value).apply()

    var autoStartService: Boolean
        get() = prefs.getBoolean("auto_start_service", true)
        set(value) = prefs.edit().putBoolean("auto_start_service", value).apply()

    var isSmoothnessEnabled: Boolean
        get() = prefs.getBoolean("smoothness_enabled", false)
        set(value) = prefs.edit().putBoolean("smoothness_enabled", value).apply()

    // 0 = LOW, 1 = MEDIUM, 2 = HIGH
    var smoothnessLevel: Int
        get() = prefs.getInt("smoothness_level", 1)
        set(value) = prefs.edit().putInt("smoothness_level", value).apply()
}
