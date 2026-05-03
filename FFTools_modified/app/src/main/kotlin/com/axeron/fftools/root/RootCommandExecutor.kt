// app/src/main/kotlin/com/axeron/fftools/root/RootCommandExecutor.kt
package com.axeron.fftools.root

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RootCommandExecutor {

    /**
     * Jalankan command via su, return output sebagai String
     */
    suspend fun exec(command: String): RootResult = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val output  = process.inputStream.bufferedReader().readText().trim()
            val error   = process.errorStream.bufferedReader().readText().trim()
            val code    = process.waitFor()

            if (code == 0) RootResult.Success(output)
            else           RootResult.Error("Exit $code: $error")
        } catch (e: Exception) {
            RootResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Jalankan beberapa command sekaligus dalam satu su session
     */
    suspend fun execMultiple(commands: List<String>): RootResult = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec("su")
            val writer  = process.outputStream.bufferedWriter()
            commands.forEach { cmd ->
                writer.write("$cmd\n")
            }
            writer.write("exit\n")
            writer.flush()
            writer.close()

            val output = process.inputStream.bufferedReader().readText().trim()
            val code   = process.waitFor()

            if (code == 0) RootResult.Success(output)
            else           RootResult.Error("Exit code: $code")
        } catch (e: Exception) {
            RootResult.Error(e.message ?: "Unknown error")
        }
    }
}

sealed class RootResult {
    data class Success(val output: String) : RootResult()
    data class Error(val message: String)  : RootResult()

    val isSuccess get() = this is Success
}
