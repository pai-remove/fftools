// app/src/main/kotlin/com/axeron/fftools/root/RootFeatureUnlocker.kt
package com.axeron.fftools.root

/**
 * Utility untuk mengecek apakah fitur tertentu bisa dijalankan
 * berdasarkan ketersediaan root/Shizuku
 */
object RootFeatureUnlocker {

    /**
     * Return true jika device bisa menjalankan privileged features
     */
    fun canUsePrivilegedFeatures(): Boolean {
        return RootChecker.isRooted()
    }

    /**
     * Return string deskripsi mode yang tersedia
     */
    fun getPrivilegeDescription(): String {
        return when {
            RootChecker.isRooted() -> "Root — semua fitur tersedia"
            else -> "Tidak ada privilege — fitur terbatas"
        }
    }
}
