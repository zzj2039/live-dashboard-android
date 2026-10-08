package com.example.livedashboardandroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.livedashboardandroid.api.ApiClient
import com.example.livedashboardandroid.api.DeviceState
import com.example.livedashboardandroid.repository.DashboardRepository
import com.example.livedashboardandroid.databinding.ActivityMainBinding
import com.example.livedashboardandroid.ui.DashboardViewModel
import com.example.livedashboardandroid.ui.DeviceAdapter
import com.example.livedashboardandroid.ui.TimelineAdapter
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: DashboardViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(DashboardRepository(ApiClient.apiService)) as T
            }
        }
    }

    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var timelineAdapter: TimelineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        loadInitialData()
    }

    private fun setupViews() {
        binding.statusBubble.setDeviceState(null)

        deviceAdapter = DeviceAdapter(
            devices = emptyList(),
            onDeviceSelected = { device ->
                viewModel.setSelectedDate(LocalDate.now())
                updateSelectedDevice(device.device_id)
            },
            selectedDeviceId = null
        )
        binding.deviceList.layoutManager = LinearLayoutManager(this)
        binding.deviceList.adapter = deviceAdapter

        timelineAdapter = TimelineAdapter(emptyList())
        binding.timelineList.layoutManager = LinearLayoutManager(this)
        binding.timelineList.adapter = timelineAdapter

        binding.datePicker.onDateSelected = { date ->
            viewModel.setSelectedDate(LocalDate.parse(date))
        }

        binding.refreshButton.setOnClickListener {
            viewModel.loadCurrentData()
            viewModel.loadTimelineData()
        }

        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.currentData.observe(this) { response ->
            response?.let { data ->
                if (data.devices.isNotEmpty()) {
                    val selected = data.devices.find { it.is_online == 1 } ?: data.devices.first()
                    binding.statusBubble.setDeviceState(selected)
                }

                deviceAdapter = DeviceAdapter(
                    devices = data.devices,
                    onDeviceSelected = { device ->
                        viewModel.setSelectedDate(LocalDate.now())
                        updateSelectedDevice(device.device_id)
                    },
                    selectedDeviceId = getSelectedDeviceId()
                )
                binding.deviceList.adapter = deviceAdapter
                updateDeviceOverview(data.devices)
            }
        }

        viewModel.timelineData.observe(this) { response ->
            response?.let { data ->
                timelineAdapter = TimelineAdapter(data.segments)
                binding.timelineList.adapter = timelineAdapter
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }
    }

    private fun loadInitialData() {
        viewModel.loadCurrentData()
        viewModel.loadTimelineData()
    }

    private fun updateSelectedDevice(deviceId: String) {
        deviceAdapter.selectedDeviceId = deviceId
        deviceAdapter.notifyDataSetChanged()
        viewModel.selectedDate.value?.let { viewModel.loadTimelineData(it) }
    }

    private fun getSelectedDeviceId(): String? = deviceAdapter.selectedDeviceId

    private fun updateDeviceOverview(devices: List<DeviceState>) {
        val onlineDevices = devices.count { it.is_online == 1 }
        binding.onlineDevices.text = "$onlineDevices/${devices.size}"
        binding.deviceOverview.text =
            if (devices.isEmpty()) "还没有设备连接呢~"
            else devices.joinToString(" · ") { it.device_name }
    }
}