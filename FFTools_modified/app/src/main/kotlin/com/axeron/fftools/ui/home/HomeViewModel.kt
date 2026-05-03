// app/src/main/kotlin/com/axeron/fftools/ui/home/HomeViewModel.kt
package com.axeron.fftools.ui.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import com.axeron.fftools.data.FeatureModel
import com.axeron.fftools.data.FeatureRepository
import com.axeron.fftools.data.PreferencesManager
import com.axeron.fftools.features.*
import com.axeron.fftools.root.RootChecker
import com.axeron.fftools.service.ForegroundService
import com.axeron.fftools.shizuku.ShizukuChecker
import com.axeron.fftools.shizuku.ShizukuStatus
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)

    // ── UI State ─────────────────────────────────────────────────
    private val _features    = MutableLiveData(FeatureRepository.getAll())
    val features: LiveData<List<FeatureModel>> = _features

    private val _privilegeStatus = MutableLiveData<PrivilegeStatus>()
    val privilegeStatus: LiveData<PrivilegeStatus> = _privilegeStatus

    private val _serviceRunning = MutableLiveData(false)
    val serviceRunning: LiveData<Boolean> = _serviceRunning

    init {
        detectPrivilege()
        loadSavedStates()
    }

    // ── Privilege Detection ───────────────────────────────────────

    fun detectPrivilege() {
        val shizukuStatus = ShizukuChecker.getStatus()
        _privilegeStatus.value = when {
            shizukuStatus == ShizukuStatus.READY -> PrivilegeStatus.SHIZUKU
            RootChecker.isRooted()               -> PrivilegeStatus.ROOT
            else                                 -> PrivilegeStatus.NONE
        }
    }

    // ── Toggle Feature ────────────────────────────────────────────

    fun toggleFeature(featureId: String, enabled: Boolean) {
        val updated = _features.value?.map { feature ->
            if (feature.id == featureId) feature.copy(isEnabled = enabled) else feature
        }
        _features.value = updated

        when (featureId) {
            "dpi"           -> prefs.isDpiEnabled          = enabled
            "headtrick"     -> prefs.isHeadtrickEnabled    = enabled
            "stabilizer"    -> prefs.isStabilizerEnabled   = enabled
            "refresh_rate"  -> prefs.isRefreshRateEnabled  = enabled
            "touch_tracker" -> prefs.isTouchTrackerEnabled = enabled
            "aspect_ratio"  -> prefs.isAspectRatioEnabled  = enabled
            "smoothness"    -> prefs.isSmoothnessEnabled   = enabled
        }

        viewModelScope.launch {
            applyFeature(featureId, enabled)
        }
    }

    private suspend fun applyFeature(id: String, enabled: Boolean) {
        when (id) {
            "dpi"          -> if (enabled) DpiManager.enable()                else DpiManager.disable()
            "headtrick"    -> if (enabled) HeadtrickManager.enable()          else HeadtrickManager.disable()
            "stabilizer"   -> if (enabled) StabilizerManager.enable()         // disable = noop (RAM returns naturally)
            "refresh_rate" -> if (enabled) RefreshRateManager.setMaxRefreshRate() else RefreshRateManager.resetRefreshRate()
            "touch_tracker"-> if (enabled) TouchTrackerManager.enable()       else TouchTrackerManager.disable()
            "aspect_ratio" -> if (enabled) AspectRatioManager.enable()        else AspectRatioManager.disable()
            "smoothness"   -> {
                if (enabled) {
                    val level = SmoothnessManager.Level.values()
                        .getOrElse(prefs.smoothnessLevel) { SmoothnessManager.Level.MEDIUM }
                    SmoothnessManager.enable(level)
                } else {
                    SmoothnessManager.disable()
                }
            }
        }
    }

    // ── Foreground Service ────────────────────────────────────────

    fun startService() {
        val ctx = getApplication<Application>()
        ctx.startForegroundService(Intent(ctx, ForegroundService::class.java))
        _serviceRunning.value = true
    }

    fun stopService() {
        val ctx = getApplication<Application>()
        ctx.stopService(Intent(ctx, ForegroundService::class.java))
        _serviceRunning.value = false
    }

    // ── Load Saved States ─────────────────────────────────────────

    private fun loadSavedStates() {
        val updated = _features.value?.map { feature ->
            val saved = when (feature.id) {
                "dpi"           -> prefs.isDpiEnabled
                "headtrick"     -> prefs.isHeadtrickEnabled
                "stabilizer"    -> prefs.isStabilizerEnabled
                "refresh_rate"  -> prefs.isRefreshRateEnabled
                "touch_tracker" -> prefs.isTouchTrackerEnabled
                "aspect_ratio"  -> prefs.isAspectRatioEnabled
                "smoothness"    -> prefs.isSmoothnessEnabled
                else            -> feature.isEnabled
            }
            feature.copy(isEnabled = saved)
        }
        _features.value = updated
    }
    // ── Smoothness Level ──────────────────────────────────────────

    fun setSmoothnessLevel(levelIndex: Int) {
        prefs.smoothnessLevel = levelIndex
        if (prefs.isSmoothnessEnabled) {
            viewModelScope.launch {
                val level = SmoothnessManager.Level.values()
                    .getOrElse(levelIndex) { SmoothnessManager.Level.MEDIUM }
                SmoothnessManager.enable(level)
            }
        }
    }

    fun getSavedSmoothnessLevel(): Int = prefs.smoothnessLevel
}

enum class PrivilegeStatus {
    SHIZUKU,
    ROOT,
    NONE
}
