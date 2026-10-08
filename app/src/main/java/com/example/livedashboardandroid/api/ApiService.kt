package com.example.livedashboardandroid.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date
import java.util.TimeZone

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

interface ApiService {
    @GET("api/current")
    suspend fun getCurrent(): CurrentResponse

    @GET("api/timeline")
    suspend fun getTimeline(
        @Query("date") date: String,
        @Query("tz") tz: Int
    ): TimelineResponse
}

object ApiClient {
    private const val BASE_URL = "https://api.example.com/" // 默认URL，实际使用时从设置中获取

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

class DateTypeAdapter : com.google.gson.JsonDeserializer<Date>, com.google.gson.JsonSerializer<Date> {
    override fun deserialize(json: com.google.gson.JsonElement, typeOfT: java.lang.reflect.Type, context: com.google.gson.JsonDeserializationContext): Date {
        return Date(json.asJsonPrimitive.asString)
    }

    override fun serialize(src: Date, typeOfSrc: java.lang.reflect.Type, context: com.google.gson.JsonSerializationContext): com.google.gson.JsonElement {
        return com.google.gson.JsonPrimitive(src.toString())
    }
}