package com.axeron.fftools.privileged

import com.axeron.fftools.shizuku.ShizukuServiceConnection

object SystemSettingsWriter {
    suspend fun putSecure(key: String, value: String) {
        ShizukuServiceConnection.service?.writeSecureSetting(key, value)
    }
    suspend fun putSystem(key: String, value: String) {
        ShizukuServiceConnection.service?.writeSystemSetting(key, value)
    }
    suspend fun getSecure(key: String): String? {
        return ShizukuServiceConnection.service?.readSecureSetting(key)
    }
}
