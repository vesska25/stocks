package com.watchtower.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchtower.app.data.WatchtowerRepository
import com.watchtower.app.data.model.NewsItem
import com.watchtower.app.data.model.PricePoint
import com.watchtower.app.data.model.TickerDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val detail: TickerDetail? = null,
    val history: List<PricePoint> = emptyList(),
    val news: List<NewsItem> = emptyList(),
    val range: String = "1M",
    val signalsOpen: Boolean = true,
)

class DetailViewModel(private val repository: WatchtowerRepository, private val ticker: String) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val detailResult = repository.getTickerDetail(ticker)
            val historyResult = repository.getTickerHistory(ticker, _uiState.value.range)
            val newsResult = repository.getTickerNews(ticker)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = detailResult.exceptionOrNull()?.message,
                detail = detailResult.getOrNull(),
                history = historyResult.getOrDefault(emptyList()),
                news = newsResult.getOrDefault(emptyList()),
            )
        }
    }

    fun setRange(range: String) {
        _uiState.value = _uiState.value.copy(range = range)
        viewModelScope.launch {
            val result = repository.getTickerHistory(ticker, range)
            _uiState.value = _uiState.value.copy(history = result.getOrDefault(emptyList()))
        }
    }

    fun toggleSignals() {
        _uiState.value = _uiState.value.copy(signalsOpen = !_uiState.value.signalsOpen)
    }
}
