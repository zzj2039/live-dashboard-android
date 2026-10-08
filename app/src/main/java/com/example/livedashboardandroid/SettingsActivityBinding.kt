package com.example.livedashboardandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.viewbinding.ViewBinding
import com.example.livedashboardandroid.databinding.SettingsActivityBinding

@Keep
class SettingsActivityBinding private constructor(val settingsActivity: SettingsActivity) : ViewBinding {
    val serverUrlEditText: androidx.appcompat.widget.AppCompatEditText = settingsActivity.findViewById(com.example.livedashboardandroid.R.id.serverUrlEditText)
    val apiKeyEditText: androidx.appcompat.widget.AppCompatEditText = settingsActivity.findViewById(com.example.livedashboardandroid.R.id.apiKeyEditText)
    val saveSettingsButton: androidx.appcompat.widget.AppCompatButton = settingsActivity.findViewById(com.example.livedashboardandroid.R.id.saveSettingsButton)

    override fun getRoot(): View = settingsActivity

    companion object {
        fun inflate(inflater: LayoutInflater): SettingsActivityBinding {
            val view = inflater.inflate(com.example.livedashboardandroid.R.layout.settings_activity, null, false)
            return SettingsActivityBinding(view as SettingsActivity)
        }

        fun bind(view: SettingsActivity): SettingsActivityBinding {
            return SettingsActivityBinding(view)
        }
    }
}