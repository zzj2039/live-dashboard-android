package com.example.livedashboardandroid.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.livedashboardandroid.R
import com.example.livedashboardandroid.api.DeviceState
import java.text.SimpleDateFormat
import java.util.Locale

class StatusBubble @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val statusText: TextView
    private val detailText: TextView
    private val musicText: TextView
    private val batteryText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.status_bubble, this, true)
        
        statusText = findViewById(R.id.statusText)
        detailText = findViewById(R.id.detailText)
        musicText = findViewById(R.id.musicText)
        batteryText = findViewById(R.id.batteryText)
    }

    fun setDeviceState(deviceState: DeviceState?) {
        if (deviceState == null) {
            // 离线状态
            statusText.text = "(-.-)zzZ"
            detailText.text = "不在电脑前喵~"
            detailText.visibility = TextView.VISIBLE
            musicText.visibility = TextView.GONE
            batteryText.visibility = TextView.GONE
            return
        }

        val isOnline = deviceState.is_online == 1
        if (!isOnline) {
            // 离线状态
            statusText.text = "(-.-)zzZ"
            detailText.text = "${deviceState.device_name} 不在电脑前喵~"
            detailText.visibility = TextView.VISIBLE
            musicText.visibility = TextView.GONE
            batteryText.visibility = TextView.GONE
            return
        }

        // 在线状态
        statusText.text = deviceState.status_text ?: "正在忙别的喵~"
        detailText.visibility = TextView.GONE
        musicText.visibility = TextView.GONE
        batteryText.visibility = TextView.GONE

        // 显示详情
        if (!deviceState.display_title.isNullOrEmpty()) {
            detailText.text = "「${deviceState.display_title}」"
            detailText.visibility = TextView.VISIBLE
        }

        // 显示音乐信息
        if (deviceState.extra?.music != null) {
            val music = deviceState.extra.music
            val musicTextContent = if (music?.artist != null) {
                "${music.artist} - ${music.title}"
            } else {
                music?.title ?: ""
            }
            musicText.text = "♪ 正在听：$musicTextContent"
            musicText.visibility = TextView.VISIBLE
        }

        // 显示电池信息
        if (deviceState.extra?.battery_percent != null) {
            val batteryPercent = deviceState.extra.battery_percent
            val charging = deviceState.extra.battery_charging ?: false
            val chargingIcon = if (charging) "⚡" else "🔋"
            batteryText.text = "$chargingIcon $batteryPercent%"
            batteryText.visibility = TextView.VISIBLE
        }
    }
}