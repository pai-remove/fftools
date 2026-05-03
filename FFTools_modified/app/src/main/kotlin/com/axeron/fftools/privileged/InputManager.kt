package com.axeron.fftools.privileged

import com.axeron.fftools.shizuku.ShizukuServiceConnection

object InputManager {
    suspend fun setTouchSensitivity(level: Int) {
        ShizukuServiceConnection.service?.setTouchSensitivity(level)
    }
    suspend fun writeSecureSetting(key: String, value: String) {
        ShizukuServiceConnection.service?.writeSecureSetting(key, value)
    }
    suspend fun writeSystemSetting(key: String, value: String) {
        ShizukuServiceConnection.service?.writeSystemSetting(key, value)
    }
}
