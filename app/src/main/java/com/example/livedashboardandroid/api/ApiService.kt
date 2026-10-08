package com.example.livedashboardandroid.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/current")
    suspend fun getCurrent(): CurrentResponse

    @GET("api/timeline")
    suspend fun getTimeline(
        @Query("date") date: String,
        @Query("tz") tz: Int
    ): TimelineResponse
}