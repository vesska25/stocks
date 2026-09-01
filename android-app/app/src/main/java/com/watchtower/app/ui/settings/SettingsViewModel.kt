package com.watchtower.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchtower.app.data.settings.AppSettings
import com.watchtower.app.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _draft = MutableStateFlow(AppSettings())
    val draft: StateFlow<AppSettings> = _draft

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    init {
        viewModelScope.launch {
            _draft.value = settingsRepository.settingsFlow.first()
        }
    }

    fun updateBaseUrl(value: String) {
        _draft.value = _draft.value.copy(baseUrl = value)
        _saved.value = false
    }

    fun updateApiKey(value: String) {
        _draft.value = _draft.value.copy(apiKey = value)
        _saved.value = false
    }

    fun save() {
        viewModelScope.launch {
            settingsRepository.save(_draft.value)
            _saved.value = true
        }
    }
}
