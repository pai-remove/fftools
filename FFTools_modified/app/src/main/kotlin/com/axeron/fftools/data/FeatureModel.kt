// app/src/main/kotlin/com/axeron/fftools/data/FeatureModel.kt
package com.axeron.fftools.data

data class FeatureModel(
    val id: String,
    val title: String,
    val description: String,
    val isEnabled: Boolean = false,
    val requiresPrivilege: Boolean = true
)

object FeatureRepository {
    fun getAll(): List<FeatureModel> = listOf(
        FeatureModel(
            id = "dpi",
            title = "DPI Optimizer",
            description = "Turunkan DPI saat FF berjalan untuk performa lebih baik"
        ),
        FeatureModel(
            id = "headtrick",
            title = "Headtrick",
            description = "Kurangi pointer speed & touch slop agar aim lebih presisi"
        ),
        FeatureModel(
            id = "stabilizer",
            title = "RAM Stabilizer",
            description = "Kill background apps & trim memory agar FF dapat lebih banyak RAM"
        ),
        FeatureModel(
            id = "refresh_rate",
            title = "Max Refresh Rate",
            description = "Paksa refresh rate ke nilai tertinggi yang didukung device"
        ),
        FeatureModel(
            id = "touch_tracker",
            title = "Touch Tracker",
            description = "Tingkatkan sensitivitas sentuh untuk respons lebih cepat"
        ),
        FeatureModel(
            id = "aspect_ratio",
            title = "Aspect Ratio",
            description = "Optimasi density layar agar FOV lebih luas di FF"
        ),
        FeatureModel(
            id = "smoothness",
            title = "Kelicinan Layar",
            description = "Kombinasi pointer speed + touch dispatch untuk gerakan lebih licin"
        )
    )
}
