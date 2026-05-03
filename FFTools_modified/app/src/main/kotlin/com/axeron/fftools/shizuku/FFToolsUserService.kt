// app/src/main/kotlin/com/axeron/fftools/shizuku/FFToolsUserService.kt
package com.axeron.fftools.shizuku

import android.annotation.SuppressLint
import com.axeron.fftools.IFFToolsService

/**
 * Service ini dijalankan oleh Shizuku di dalam proses shell/root UID.
 * Semua operasi privileged dilakukan di sini — bukan di proses app biasa.
 */
class FFToolsUserService : IFFToolsService.Stub() {

    // ── DPI ──────────────────────────────────────────────────────

    override fun setDpi(dpi: Int) {
        runShell("wm density $dpi")
    }

    override fun getCurrentDpi(): Int {
        val output = runShellOutput("wm density")
        val match = Regex("Override density: (\\d+)").find(output)
            ?: Regex("Physical density: (\\d+)").find(output)
        return match?.groupValues?.get(1)?.toIntOrNull() ?: -1
    }

    override fun resetDpi() {
        runShell("wm density reset")
    }

    // ── Refresh Rate ──────────────────────────────────────────────

    override fun setRefreshRate(rate: Int) {
        writeSecureSetting("min_refresh_rate", rate.toString())
        writeSecureSetting("peak_refresh_rate", rate.toString())
    }

    override fun getMaxRefreshRate(): Int {
        val output = runShellOutput("dumpsys display | grep mRefreshRate")
        val match = Regex("mRefreshRate=(\\d+\\.?\\d*)").find(output)
        return match?.groupValues?.get(1)?.toDoubleOrNull()?.toInt() ?: 60
    }

    // ── Background Apps ───────────────────────────────────────────

    override fun killBackgroundApps() {
        val output = runShellOutput("dumpsys activity processes | grep 'packageList'")
        val packages = Regex("\\{([a-z][a-z0-9._]+)\\}").findAll(output)
            .map { it.groupValues[1] }
            .filter { pkg ->
                !pkg.startsWith("com.android") &&
                !pkg.startsWith("android") &&
                pkg != "com.axeron.fftools" &&
                pkg != "com.dts.freefireth" &&
                pkg != "com.garena.game.ffthai" &&
                pkg != "com.dts.freefiremax"
            }
            .toSet()

        packages.forEach { pkg -> runShell("am force-stop $pkg") }
        runShell("am send-trim-memory all 80")
    }

    // ── Touch Sensitivity ─────────────────────────────────────────

    @SuppressLint("PrivateApi")
    override fun setTouchSensitivity(level: Int) {
        val pointerSpeed = when (level) {
            1    -> "1"
            2    -> "3"
            else -> "0"
        }
        val touchSlop = when (level) {
            1    -> "6"
            2    -> "4"
            else -> "8"
        }
        writeSystemSetting("pointer_speed", pointerSpeed)
        writeSecureSetting("touch_slop", touchSlop)
    }

    // ── Settings ──────────────────────────────────────────────────

    @SuppressLint("PrivateApi")
    override fun writeSecureSetting(key: String, value: String) {
        runShell("settings put secure $key $value")
    }

    @SuppressLint("PrivateApi")
    override fun writeSystemSetting(key: String, value: String) {
        runShell("settings put system $key $value")
    }

    override fun readSecureSetting(key: String): String {
        return runShellOutput("settings get secure $key").trim()
    }

    // ── Aspect Ratio / Display Density ────────────────────────────

    override fun setDisplayDensity(dpi: Int) {
        runShell("wm density $dpi")
    }

    override fun resetDisplayDensity() {
        runShell("wm density reset")
    }

    // ── Utils ─────────────────────────────────────────────────────

    override fun getUid(): Int = android.os.Process.myUid()

    override fun isAlive(): Boolean = true

    // ── Shell Helpers ─────────────────────────────────────────────

    private fun runShell(cmd: String) {
        try {
            val process = Runtime.getRuntime().exec(cmd)
            process.waitFor() // FIX: tunggu sampai selesai
        } catch (_: Exception) {}
    }

    private fun runShellOutput(cmd: String): String {
        return try {
            val process = Runtime.getRuntime().exec(cmd)
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            output
        } catch (_: Exception) {
            ""
        }
    }
}
