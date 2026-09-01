package com.watchtower.app.data.network

import com.watchtower.app.data.settings.SettingsRepository
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Base URL and API key are user-configurable at runtime (Settings screen),
 * so Retrofit is built once against a placeholder base URL and this
 * interceptor rewrites the scheme/host/port per request from current
 * DataStore settings, and attaches X-API-Key. Path and query from the
 * Retrofit-resolved URL are left untouched.
 */
class ApiKeyInterceptor(private val settingsRepository: SettingsRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val settings = settingsRepository.currentSettingsBlocking()
        val original = chain.request()

        val configuredUrl = settings.baseUrl.toHttpUrlOrNull()
        val requestBuilder = original.newBuilder()
            .header("X-API-Key", settings.apiKey)

        if (configuredUrl != null) {
            val newUrl = original.url.newBuilder()
                .scheme(configuredUrl.scheme)
                .host(configuredUrl.host)
                .port(configuredUrl.port)
                .build()
            requestBuilder.url(newUrl)
        }

        return chain.proceed(requestBuilder.build())
    }
}
