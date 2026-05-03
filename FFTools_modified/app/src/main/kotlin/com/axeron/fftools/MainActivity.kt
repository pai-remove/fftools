// app/src/main/kotlin/com/axeron/fftools/MainActivity.kt
package com.axeron.fftools

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.axeron.fftools.shizuku.ShizukuChecker
import com.axeron.fftools.shizuku.ShizukuPermissionHelper
import com.axeron.fftools.shizuku.ShizukuServiceConnection
import com.axeron.fftools.shizuku.ShizukuStatus
import com.google.android.material.bottomnavigation.BottomNavigationView
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    // Listener: Shizuku service connect/disconnect
    private val shizukuBinderListener = Shizuku.OnBinderReceivedListener {
        checkShizuku()
    }
    private val shizukuDeadListener = Shizuku.OnBinderDeadListener {
        ShizukuServiceConnection.unbind()
        Toast.makeText(this, "Shizuku terputus", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
        setupShizuku()
    }

    private fun setupNavigation() {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)
    }

    private fun setupShizuku() {
        // Register listeners
        Shizuku.addBinderReceivedListenerSticky(shizukuBinderListener)
        Shizuku.addBinderDeadListener(shizukuDeadListener)
        ShizukuPermissionHelper.register()

        ShizukuPermissionHelper.onGranted = {
            ShizukuServiceConnection.bind()
            Toast.makeText(this, "Shizuku terhubung ✓", Toast.LENGTH_SHORT).show()
        }
        ShizukuPermissionHelper.onDenied = {
            Toast.makeText(
                this,
                "Izin Shizuku ditolak — beberapa fitur tidak tersedia",
                Toast.LENGTH_LONG
            ).show()
        }

        ShizukuServiceConnection.onConnected = {
            // Service berhasil bind — UI bisa update status
        }
        ShizukuServiceConnection.onDisconnected = {
            Toast.makeText(this, "Shizuku service disconnect", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkShizuku() {
        when (ShizukuChecker.getStatus()) {
            ShizukuStatus.READY       -> ShizukuServiceConnection.bind()
            ShizukuStatus.NOT_GRANTED -> ShizukuPermissionHelper.requestPermission()
            ShizukuStatus.NOT_RUNNING -> { /* tampilkan banner "install Shizuku" */ }
        }
    }

    override fun onDestroy() {
        Shizuku.removeBinderReceivedListener(shizukuBinderListener)
        Shizuku.removeBinderDeadListener(shizukuDeadListener)
        ShizukuPermissionHelper.unregister()
        ShizukuServiceConnection.unbind()
        super.onDestroy()
    }
}
