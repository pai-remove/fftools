package com.axeron.fftools.privileged

import com.axeron.fftools.shizuku.ShizukuServiceConnection

object PackageController {
    suspend fun killBackgroundApps() {
        ShizukuServiceConnection.service?.killBackgroundApps()
    }
}
