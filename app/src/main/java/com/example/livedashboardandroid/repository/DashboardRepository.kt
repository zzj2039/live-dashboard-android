package com.example.livedashboardandroid.repository

import com.example.livedashboardandroid.api.ApiService
import com.example.livedashboardandroid.api.CurrentResponse
import com.example.livedashboardandroid.api.TimelineResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class DashboardRepository(private val apiService: ApiService) {

    suspend fun getCurrent(): CurrentResponse = apiService.getCurrent()

    suspend fun getTimeline(date: LocalDate): TimelineResponse {
        val tzOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (1000 * 60)
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return apiService.getTimeline(dateString, tzOffset)
    }

    fun getCurrentFlow(): Flow<CurrentResponse> = flow {
        while (true) {
            try {
                emit(apiService.getCurrent())
                delay(10000)
            } catch (e: Exception) {
                delay(5000)
            }
        }
    }

    fun getTimelineFlow(date: LocalDate): Flow<TimelineResponse> = flow {
        while (true) {
            try {
                emit(getTimeline(date))
                delay(30000)
            } catch (e: Exception) {
                delay(5000)
            }
        }
    }
}
