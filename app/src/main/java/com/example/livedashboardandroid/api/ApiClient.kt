package com.example.livedashboardandroid.api

import android.content.Context
import com.example.livedashboardandroid.LiveDashboardApplication
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date
import java.util.TimeZone

object ApiClient {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
        .create()

    private suspend fun getServerUrlSuspend(context: Context): String = withContext(Dispatchers.IO) {
        LiveDashboardApplication.getServerUrl(context)
    }

    private suspend fun getApiTokenSuspend(context: Context): String = withContext(Dispatchers.IO) {
        LiveDashboardApplication.getApiToken(context)
    }

    private fun getOkHttpClient(context: Context): OkHttpClient {
        val serverUrl = getServerUrlSuspend(context)
        val apiKey = getApiTokenSuspend(context)

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                
                // 添加 API 密钥到请求头
                if (apiKey.isNotEmpty()) {
                    requestBuilder.header("Authorization", "Bearer $apiKey")
                }
                
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val retrofit: Retrofit by lazy {
        val context = com.example.livedashboardandroid.LiveDashboardApplication.instance
        Retrofit.Builder()
            .baseUrl(getServerUrlSuspend(context))
            .client(getOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun getServerUrl(context: Context): String {
        return getServerUrlSuspend(context)
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

class DateTypeAdapter : com.google.gson.JsonDeserializer<Date>, com.google.gson.JsonSerializer<Date> {
    override fun deserialize(json: com.google.gson.JsonElement, typeOfT: java.lang.reflect.Type, context: com.google.gson.JsonDeserializationContext): Date {
        return Date(json.asJsonPrimitive.asString)
    }

    override fun serialize(src: Date, typeOfSrc: java.lang.reflect.Type, context: com.google.gson.JsonSerializationContext): com.google.gson.JsonElement {
        return com.google.gson.JsonPrimitive(src.toString())
    }
}