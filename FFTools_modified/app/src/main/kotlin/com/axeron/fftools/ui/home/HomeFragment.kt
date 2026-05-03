// app/src/main/kotlin/com/axeron/fftools/ui/home/HomeFragment.kt
package com.axeron.fftools.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.axeron.fftools.R
import com.axeron.fftools.data.FeatureModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var tvPrivilegeStatus: TextView
    private lateinit var tvServiceStatus: TextView
    private lateinit var btnToggleService: Button
    private lateinit var containerFeatures: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPrivilegeStatus = view.findViewById(R.id.tv_privilege_status)
        tvServiceStatus   = view.findViewById(R.id.tv_service_status)
        btnToggleService  = view.findViewById(R.id.btn_toggle_service)
        containerFeatures = view.findViewById(R.id.container_features)

        setupObservers()
        setupServiceButton()
    }

    override fun onResume() {
        super.onResume()
        viewModel.detectPrivilege()
    }

    private fun setupObservers() {
        viewModel.privilegeStatus.observe(viewLifecycleOwner) { status ->
            tvPrivilegeStatus.text = when (status) {
                PrivilegeStatus.SHIZUKU -> "✓ Shizuku terhubung"
                PrivilegeStatus.ROOT    -> "✓ Root terdeteksi"
                PrivilegeStatus.NONE    -> "⚠ Tidak ada privilege — mode terbatas"
            }
            tvPrivilegeStatus.setTextColor(
                if (status == PrivilegeStatus.NONE)
                    requireContext().getColor(android.R.color.holo_orange_dark)
                else
                    requireContext().getColor(android.R.color.holo_green_dark)
            )
        }

        viewModel.serviceRunning.observe(viewLifecycleOwner) { running ->
            tvServiceStatus.text  = if (running) "Service: Aktif ✓" else "Service: Nonaktif"
            btnToggleService.text = if (running) "Stop Service" else "Mulai Service"
        }

        viewModel.features.observe(viewLifecycleOwner) { features ->
            renderFeatures(features)
        }
    }

    private fun setupServiceButton() {
        btnToggleService.setOnClickListener {
            val running = viewModel.serviceRunning.value ?: false
            if (running) viewModel.stopService() else viewModel.startService()
        }
    }

    private fun renderFeatures(features: List<FeatureModel>) {
        containerFeatures.removeAllViews()
        features.forEach { feature ->
            if (feature.id == "smoothness") {
                renderSmoothnessCard(feature)
            } else {
                val card = layoutInflater.inflate(R.layout.item_feature_card, containerFeatures, false)
                card.findViewById<TextView>(R.id.tv_feature_title).text = feature.title
                card.findViewById<TextView>(R.id.tv_feature_desc).text  = feature.description

                val toggle = card.findViewById<Switch>(R.id.switch_feature)
                toggle.setOnCheckedChangeListener(null)
                toggle.isChecked = feature.isEnabled
                toggle.setOnCheckedChangeListener { _, checked ->
                    viewModel.toggleFeature(feature.id, checked)
                }

                containerFeatures.addView(card)
            }
        }
    }

    private fun renderSmoothnessCard(feature: FeatureModel) {
        val card = layoutInflater.inflate(R.layout.item_smoothness_card, containerFeatures, false)

        val toggle        = card.findViewById<Switch>(R.id.switch_smoothness)
        val levelLayout   = card.findViewById<android.view.View>(R.id.layout_smoothness_levels)
        val btnLow        = card.findViewById<Button>(R.id.btn_level_low)
        val btnMedium     = card.findViewById<Button>(R.id.btn_level_medium)
        val btnHigh       = card.findViewById<Button>(R.id.btn_level_high)

        // Restore saved state
        toggle.setOnCheckedChangeListener(null)
        toggle.isChecked = feature.isEnabled
        levelLayout.visibility = if (feature.isEnabled) android.view.View.VISIBLE else android.view.View.GONE

        // Highlight active level
        val savedLevel = viewModel.getSavedSmoothnessLevel()
        highlightLevel(savedLevel, btnLow, btnMedium, btnHigh)

        toggle.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleFeature("smoothness", checked)
            levelLayout.visibility = if (checked) android.view.View.VISIBLE else android.view.View.GONE
        }

        btnLow.setOnClickListener {
            viewModel.setSmoothnessLevel(0)
            highlightLevel(0, btnLow, btnMedium, btnHigh)
        }
        btnMedium.setOnClickListener {
            viewModel.setSmoothnessLevel(1)
            highlightLevel(1, btnLow, btnMedium, btnHigh)
        }
        btnHigh.setOnClickListener {
            viewModel.setSmoothnessLevel(2)
            highlightLevel(2, btnLow, btnMedium, btnHigh)
        }

        containerFeatures.addView(card)
    }

    private fun highlightLevel(level: Int, btnLow: Button, btnMedium: Button, btnHigh: Button) {
        val activeAlpha  = 1.0f
        val inactiveAlpha = 0.4f
        btnLow.alpha    = if (level == 0) activeAlpha else inactiveAlpha
        btnMedium.alpha = if (level == 1) activeAlpha else inactiveAlpha
        btnHigh.alpha   = if (level == 2) activeAlpha else inactiveAlpha
    }
}
