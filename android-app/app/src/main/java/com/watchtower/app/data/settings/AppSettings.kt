package com.watchtower.app.data.settings

data class AppSettings(
    val baseUrl: String = "",
    val apiKey: String = "",
) {
    val isConfigured: Boolean
        get() = baseUrl.isNotBlank() && apiKey.isNotBlank()
}
