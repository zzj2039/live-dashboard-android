package com.example.livedashboardandroid

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LiveDashboardApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val SERVER_URL_KEY = stringPreferencesKey("server_url")
        val API_TOKEN_KEY = stringPreferencesKey("api_token")

        lateinit var instance: LiveDashboardApplication
            private set

        suspend fun getServerUrl(context: Context): String {
            val preferences = context.dataStore.data.first()
            return preferences[SERVER_URL_KEY] ?: ""
        }

        suspend fun getApiToken(context: Context): String {
            val preferences = context.dataStore.data.first()
            return preferences[API_TOKEN_KEY] ?: ""
        }

        suspend fun setServerUrl(context: Context, url: String) {
            context.dataStore.edit { preferences ->
                preferences[SERVER_URL_KEY] = url
            }
        }

        suspend fun setApiToken(context: Context, token: String) {
            context.dataStore.edit { preferences ->
                preferences[API_TOKEN_KEY] = token
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}