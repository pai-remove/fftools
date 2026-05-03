// app/src/main/kotlin/com/axeron/fftools/ui/about/AboutFragment.kt
package com.axeron.fftools.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.axeron.fftools.R

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_version).text = "Versi 1.0.0"
        view.findViewById<TextView>(R.id.tv_about_text).text =
            "FFTools oleh Axeron\n\nAplikasi optimizer Free Fire yang menggunakan Shizuku atau Root " +
            "untuk mengoptimalkan performa device saat bermain Free Fire.\n\n" +
            "Fitur:\n• DPI Optimizer\n• Headtrick\n• RAM Stabilizer\n• Max Refresh Rate\n• Touch Tracker\n• Aspect Ratio"
    }
}
