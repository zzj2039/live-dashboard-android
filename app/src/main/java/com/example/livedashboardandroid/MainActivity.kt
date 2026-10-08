package com.example.livedashboardandroid

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.livedashboardandroid.api.ApiService
import com.example.livedashboardandroid.repository.DashboardRepository
import com.example.livedashboardandroid.ui.DashboardViewModel
import com.example.livedashboardandroid.ui.DeviceAdapter
import com.example.livedashboardandroid.ui.StatusBubble
import com.example.livedashboardandroid.ui.TimelineAdapter
import com.example.livedashboardandroid.databinding.ActivityMainBinding
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: DashboardViewModel by viewModels()
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
        // 设置状态气泡
        binding.statusBubble.setDeviceState(null)

        // 设置设备列表
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

        // 设置时间线
        timelineAdapter = TimelineAdapter(emptyList())
        binding.timelineList.layoutManager = LinearLayoutManager(this)
        binding.timelineList.adapter = timelineAdapter

        // 设置日期选择器
        binding.datePicker.onDateSelected = { date ->
            viewModel.setSelectedDate(LocalDate.parse(date))
        }

        // 设置刷新按钮
        binding.refreshButton.setOnClickListener {
            viewModel.loadCurrentData()
            viewModel.loadTimelineData()
        }
    }

    private fun observeViewModel() {
        viewModel.currentData.observe(this) { currentResponse ->
            currentResponse?.let { response ->
                // 更新状态气泡
                if (response.devices.isNotEmpty()) {
                    val selectedDevice = response.devices.find { it.is_online == 1 } ?: response.devices.first()
                    binding.statusBubble.setDeviceState(selectedDevice)
                }

                // 更新设备列表
                deviceAdapter = DeviceAdapter(
                    devices = response.devices,
                    onDeviceSelected = { device ->
                        viewModel.setSelectedDate(LocalDate.now())
                        updateSelectedDevice(device.device_id)
                    },
                    selectedDeviceId = getSelectedDeviceId()
                )
                binding.deviceList.adapter = deviceAdapter

                // 更新设备概览
                updateDeviceOverview(response.devices)
            }
        }

        viewModel.timelineData.observe(this) { timelineResponse ->
            timelineResponse?.let { response ->
                // 更新时间线
                timelineAdapter = TimelineAdapter(response.segments)
                binding.timelineList.adapter = timelineAdapter
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadInitialData() {
        viewModel.loadCurrentData()
        viewModel.loadTimelineData()
    }

    private fun updateSelectedDevice(deviceId: String) {
        deviceAdapter.selectedDeviceId = deviceId
        deviceAdapter.notifyDataSetChanged()
        
        // 更新时间线数据
        val currentDate = viewModel.selectedDate.value
        viewModel.loadTimelineData(currentDate)
    }

    private fun getSelectedDeviceId(): String? {
        return deviceAdapter.selectedDeviceId
    }

    private fun updateDeviceOverview(devices: List<com.example.livedashboardandroid.api.DeviceState>) {
        val onlineDevices = devices.count { it.is_online == 1 }
        val totalDevices = devices.size
        
        binding.onlineDevices.text = "$onlineDevices/$totalDevices"
        
        if (devices.isEmpty()) {
            binding.deviceOverview.text = "还没有设备连接呢~"
        } else {
            val deviceNames = devices.map { it.device_name }.joinToString(" · ")
            binding.deviceOverview.text = deviceNames
        }
    }
}