package com.example.livedashboardandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.livedashboardandroid.databinding.SettingsActivityBinding
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSettings()

        binding.saveSettingsButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            val serverUrl = LiveDashboardApplication.getServerUrl(this@SettingsActivity)
            val apiKey = LiveDashboardApplication.getApiToken(this@SettingsActivity)
            binding.serverUrlEditText.setText(serverUrl)
            binding.apiKeyEditText.setText(apiKey)
        }
    }

    private fun saveSettings() {
        val serverUrl = binding.serverUrlEditText.text.toString().trim()
        val apiKey = binding.apiKeyEditText.text.toString().trim()

        if (serverUrl.isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_url), Toast.LENGTH_SHORT).show()
            return
        }

        if (apiKey.isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_api_key), Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            LiveDashboardApplication.setServerUrl(this@SettingsActivity, serverUrl)
            LiveDashboardApplication.setApiToken(this@SettingsActivity, apiKey)
            
            Toast.makeText(this@SettingsActivity, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
            
            // 返回主界面
            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}