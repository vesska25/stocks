package com.watchtower.app.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "watchtower_settings")

/**
 * Base URL and API key are user-entered (no fixed public server — this is a
 * personal project pointed at a self-hosted API), so they live in DataStore
 * rather than BuildConfig, editable from the in-app Settings screen without
 * a rebuild.
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val BASE_URL = stringPreferencesKey("base_url")
        val API_KEY = stringPreferencesKey("api_key")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            baseUrl = prefs[Keys.BASE_URL] ?: "",
            apiKey = prefs[Keys.API_KEY] ?: "",
        )
    }

    suspend fun save(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BASE_URL] = settings.baseUrl.trim().trimEnd('/')
            prefs[Keys.API_KEY] = settings.apiKey.trim()
        }
    }

    /** Synchronous read for the OkHttp interceptor, which runs off the main thread. */
    fun currentSettingsBlocking(): AppSettings = runBlocking { settingsFlow.first() }
}
