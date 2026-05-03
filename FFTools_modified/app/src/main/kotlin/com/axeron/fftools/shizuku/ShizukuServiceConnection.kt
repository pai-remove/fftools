// app/src/main/kotlin/com/axeron/fftools/shizuku/ShizukuServiceConnection.kt
package com.axeron.fftools.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.axeron.fftools.IFFToolsService
import rikka.shizuku.Shizuku

object ShizukuServiceConnection {

    var service: IFFToolsService? = null
        private set

    var onConnected:    (() -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null

    private val userServiceArgs = Shizuku.UserServiceArgs(
        ComponentName("com.axeron.fftools", FFToolsUserService::class.java.name)
    )
        .daemon(false)          // jangan jalan terus di background
        .processNameSuffix("service")
        .debuggable(false)
        .version(1)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder != null && binder.pingBinder()) {
                service = IFFToolsService.Stub.asInterface(binder)
                onConnected?.invoke()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            onDisconnected?.invoke()
        }
    }

    fun bind() {
        if (!ShizukuChecker.isGranted()) return
        try {
            Shizuku.bindUserService(userServiceArgs, connection)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unbind() {
        try {
            Shizuku.unbindUserService(userServiceArgs, connection, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        service = null
    }

    fun isConnected(): Boolean = service?.isAlive == true
}
