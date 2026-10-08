package com.example.livedashboardandroid.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.livedashboardandroid.R
import com.example.livedashboardandroid.api.DeviceState

class DeviceAdapter(
    private val devices: List<DeviceState>,
    private val onDeviceSelected: (DeviceState) -> Unit,
    var selectedDeviceId: String? = null
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_card, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount() = devices.size

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        private val deviceStatus: TextView = itemView.findViewById(R.id.deviceStatus)
        private val deviceIcon: ImageView = itemView.findViewById(R.id.deviceIcon)
        private val selectedIndicator: ImageView = itemView.findViewById(R.id.selectedIndicator)

        fun bind(device: DeviceState) {
            deviceName.text = device.device_name
            deviceStatus.text = if (device.is_online == 1) {
                device.app_name.ifEmpty { "在线" }
            } else {
                "离线"
            }

            val iconRes = when (device.platform.lowercase()) {
                "windows" -> R.drawable.ic_windows
                "android" -> R.drawable.ic_android
                else -> R.drawable.ic_computer
            }
            deviceIcon.setImageResource(iconRes)

            selectedIndicator.visibility =
                if (device.device_id == selectedDeviceId) View.VISIBLE else View.GONE

            itemView.setOnClickListener { onDeviceSelected(device) }
        }
    }
}
