package com.example.livedashboardandroid.api

import java.util.Date

data class DeviceState(
    val device_id: String,
    val device_name: String,
    val platform: String,
    val app_id: String,
    val app_name: String,
    val status_text: String?,
    val display_title: String?,
    val last_seen_at: String,
    val is_online: Int,
    val extra: Extra? = null
)

data class Extra(
    val battery_percent: Int?,
    val battery_charging: Boolean?,
    val music: Music?
)

data class Music(
    val title: String?,
    val artist: String?,
    val app: String?
)

data class ActivityRecord(
    val id: Int,
    val device_id: String,
    val device_name: String,
    val platform: String,
    val app_id: String,
    val app_name: String,
    val status_text: String?,
    val display_title: String?,
    val started_at: String
)

data class TimelineSegment(
    val app_name: String,
    val app_id: String,
    val status_text: String,
    val display_title: String?,
    val started_at: String,
    val ended_at: String?,
    val duration_minutes: Int,
    val device_id: String,
    val device_name: String
)

data class CurrentResponse(
    val devices: List<DeviceState>,
    val recent_activities: List<ActivityRecord>,
    val server_time: String,
    val viewer_count: Int
)

data class TimelineResponse(
    val date: String,
    val segments: List<TimelineSegment>,
    val summary: Map<String, Map<String, Int>>
)

class DateTypeAdapter : com.google.gson.JsonDeserializer<Date>, com.google.gson.JsonSerializer<Date> {
    override fun deserialize(json: com.google.gson.JsonElement, typeOfT: java.lang.reflect.Type, context: com.google.gson.JsonDeserializationContext): Date {
        return Date(json.asJsonPrimitive.asString)
    }

    override fun serialize(src: Date, typeOfSrc: java.lang.reflect.Type, context: com.google.gson.JsonSerializationContext): com.google.gson.JsonElement {
        return com.google.gson.JsonPrimitive(src.toString())
    }
}