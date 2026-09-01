package com.watchtower.app.data.favorites

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "watchtower_favorites")

/**
 * Pinned tickers are purely a local, per-device preference (the API is
 * read-only and has no user concept), so they live in DataStore rather than
 * going through the backend.
 */
class FavoritesRepository(private val context: Context) {

    private object Keys {
        val TICKERS = stringSetPreferencesKey("favorite_tickers")
    }

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.TICKERS] ?: emptySet()
    }

    suspend fun toggle(ticker: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.TICKERS] ?: emptySet()
            prefs[Keys.TICKERS] = if (ticker in current) current - ticker else current + ticker
        }
    }
}
