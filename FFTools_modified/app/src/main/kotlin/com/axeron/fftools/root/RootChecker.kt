// app/src/main/kotlin/com/axeron/fftools/root/RootChecker.kt
package com.axeron.fftools.root

import java.io.File

object RootChecker {

    fun isRooted(): Boolean {
        return checkSuBinary() || checkMagisk() || checkSuperuser()
    }

    private fun checkSuBinary(): Boolean {
        val paths = listOf(
            "/system/bin/su", "/system/xbin/su",
            "/sbin/su", "/su/bin/su",
            "/magisk/.core/bin/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkMagisk(): Boolean {
        return File("/sbin/.magisk").exists() ||
               File("/data/adb/magisk").exists()
    }

    private fun checkSuperuser(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c id")
            val output  = process.inputStream.bufferedReader().readText()
            process.waitFor()
            output.contains("uid=0")
        } catch (e: Exception) {
            false
        }
    }
}
