package com.watchtower.app

import android.app.Application
import com.watchtower.app.data.WatchtowerRepository
import com.watchtower.app.data.favorites.FavoritesRepository
import com.watchtower.app.data.network.NetworkModule
import com.watchtower.app.data.settings.SettingsRepository

/**
 * Manual DI (no Hilt) — single-client personal-project scope doesn't need a
 * DI framework, just a few singletons wired once at startup.
 */
class WatchtowerApplication : Application() {

    lateinit var settingsRepository: SettingsRepository
        private set
    lateinit var repository: WatchtowerRepository
        private set
    lateinit var favoritesRepository: FavoritesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(this)
        val api = NetworkModule.create(settingsRepository)
        repository = WatchtowerRepository(api)
        favoritesRepository = FavoritesRepository(this)
    }
}
