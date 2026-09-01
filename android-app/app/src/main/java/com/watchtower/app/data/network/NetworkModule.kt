package com.watchtower.app.data.network

import com.watchtower.app.data.settings.SettingsRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkModule {

    // Retrofit needs a syntactically valid base URL at build time; the real
    // host is substituted per-request by ApiKeyInterceptor from whatever the
    // user has saved in Settings.
    private const val PLACEHOLDER_BASE_URL = "http://localhost/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun create(settingsRepository: SettingsRepository): WatchtowerApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(settingsRepository))
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(PLACEHOLDER_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(WatchtowerApi::class.java)
    }
}
