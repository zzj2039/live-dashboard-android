package com.example.livedashboardandroid.repository

import com.example.livedashboardandroid.api.ApiService
import com.example.livedashboardandroid.api.CurrentResponse
import com.example.livedashboardandroid.api.TimelineResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DashboardRepository(private val apiService: ApiService) {
    
    suspend fun getCurrent(): CurrentResponse {
        return apiService.getCurrent()
    }

    suspend fun getTimeline(date: LocalDate): TimelineResponse {
        val timeZone = TimeZone.getDefault()
        val tzOffset = timeZone.getOffset(System.currentTimeMillis()) / (1000 * 60)
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return apiService.getTimeline(dateString, tzOffset)
    }

    fun getCurrentFlow(): Flow<CurrentResponse> = flow {
        while (true) {
            try {
                val response = apiService.getCurrent()
                emit(response)
                kotlinx.coroutines.delay(10000) // 10秒刷新一次
            } catch (e: Exception) {
                // 处理错误，稍后重试
                kotlinx.coroutines.delay(5000)
            }
        }
    }

    fun getTimelineFlow(date: LocalDate): Flow<TimelineResponse> = flow {
        while (true) {
            try {
                val response = getTimeline(date)
                emit(response)
                kotlinx.coroutines.delay(30000) // 30秒刷新一次
            } catch (e: Exception) {
                // 处理错误，稍后重试
                kotlinx.coroutines.delay(5000)
            }
        }
    }
}