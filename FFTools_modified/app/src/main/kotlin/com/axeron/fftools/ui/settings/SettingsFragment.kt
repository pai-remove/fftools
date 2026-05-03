// app/src/main/kotlin/com/axeron/fftools/ui/settings/SettingsFragment.kt
package com.axeron.fftools.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.axeron.fftools.R
import com.axeron.fftools.data.PreferencesManager

class SettingsFragment : Fragment() {

    private lateinit var prefs: PreferencesManager
    private lateinit var switchAutoStart: Switch
    private lateinit var seekBarDpi: SeekBar
    private lateinit var tvDpiValue: TextView
    private lateinit var spinnerHeadtrick: Spinner
    private lateinit var spinnerTouch: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesManager(requireContext())

        switchAutoStart   = view.findViewById(R.id.switch_auto_start)
        seekBarDpi        = view.findViewById(R.id.seekbar_dpi)
        tvDpiValue        = view.findViewById(R.id.tv_dpi_value)
        spinnerHeadtrick  = view.findViewById(R.id.spinner_headtrick_level)
        spinnerTouch      = view.findViewById(R.id.spinner_touch_level)

        setupAutoStart()
        setupDpiSeekBar()
        setupSpinners()
    }

    private fun setupAutoStart() {
        switchAutoStart.isChecked = prefs.autoStartService
        switchAutoStart.setOnCheckedChangeListener { _, checked ->
            prefs.autoStartService = checked
        }
    }

    private fun setupDpiSeekBar() {
        // DPI range: 160–640, step via seekbar 0–48 → *10 + 160
        val dpiValues = (160..640 step 10).toList()
        val index = dpiValues.indexOfFirst { it >= prefs.customDpi }.coerceAtLeast(0)
        seekBarDpi.max      = dpiValues.size - 1
        seekBarDpi.progress = index
        tvDpiValue.text     = "DPI: ${dpiValues[index]}"

        seekBarDpi.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val dpi = dpiValues[progress]
                tvDpiValue.text = "DPI: $dpi"
                if (fromUser) prefs.customDpi = dpi
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    private fun setupSpinners() {
        // Headtrick level
        val headtrickLevels = listOf("Normal", "Medium", "Extreme")
        spinnerHeadtrick.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, headtrickLevels).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerHeadtrick.setSelection(prefs.headtrickLevel)
        spinnerHeadtrick.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                prefs.headtrickLevel = pos
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        // Touch level
        val touchLevels = listOf("Normal", "High", "Extreme")
        spinnerTouch.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, touchLevels).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerTouch.setSelection(prefs.touchLevel)
        spinnerTouch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                prefs.touchLevel = pos
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }
}
