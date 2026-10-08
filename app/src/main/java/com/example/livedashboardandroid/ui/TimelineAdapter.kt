package com.example.livedashboardandroid.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.livedashboardandroid.R
import com.example.livedashboardandroid.api.TimelineSegment
import java.util.Locale

class TimelineAdapter(private val segments: List<TimelineSegment>) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    private val colorMap = mutableMapOf<String, String>()
    private val appColors = arrayOf(
        "#E8A0BF", "#88C9C9", "#E8B86D", "#C4A882", "#D4917B",
        "#A8C686", "#D4A0A0", "#8CB8B0", "#C9B97A", "#B89EC4"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.timeline_item, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val segment = segments[position]
        holder.bind(segment)
    }

    override fun getItemCount() = segments.size

    inner class TimelineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val currentBadge: TextView = itemView.findViewById(R.id.currentBadge)
        private val colorDot: View = itemView.findViewById(R.id.colorDot)
        private val appStatusText: TextView = itemView.findViewById(R.id.appStatusText)
        private val durationText: TextView = itemView.findViewById(R.id.durationText)

        fun bind(segment: TimelineSegment) {
            // 设置应用颜色
            val color = getAppColor(segment.app_name)
            colorDot.setBackgroundColor(android.graphics.Color.parseColor(color))
            
            // 设置当前应用标记
            if (segment.app_name == "idle") {
                currentBadge.visibility = View.VISIBLE
                colorDot.visibility = View.GONE
            } else {
                currentBadge.visibility = View.GONE
                colorDot.visibility = View.VISIBLE
            }

            // 设置应用状态文本
            appStatusText.text = segment.status_text

            // 设置持续时间
            val duration = formatDuration(segment.duration_minutes)
            durationText.text = duration
        }

        private fun getAppColor(appName: String): String {
            val existing = colorMap[appName]
            if (existing != null) return existing
            
            val color = appColors[colorMap.size % appColors.size]
            colorMap[appName] = color
            return color
        }

        private fun formatDuration(minutes: Int): String {
            return when {
                minutes < 1 -> "<1m"
                minutes < 60 -> "${minutes}m"
                else -> {
                    val hours = minutes / 60
                    val remainingMinutes = minutes % 60
                    if (remainingMinutes > 0) {
                        "${hours}h ${remainingMinutes}m"
                    } else {
                        "${hours}h"
                    }
                }
            }
        }
    }
}