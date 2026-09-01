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

private const val SMA_PERIOD = 50

data class DetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val detail: TickerDetail? = null,
    /** Always the full 1Y series (fetched once) — visibleHistory/sma50 slice it client-side per range. */
    val fullHistory: List<PricePoint> = emptyList(),
    val news: List<NewsItem> = emptyList(),
    val range: String = "1M",
    val signalsOpen: Boolean = true,
) {
    private val visibleDayCount: Int
        get() = when (range) {
            "1D" -> 2
            "1W" -> 7
            "1M" -> 30
            "1Y" -> 365
            else -> 30
        }

    val visibleHistory: List<PricePoint>
        get() = fullHistory.takeLast(visibleDayCount)

    /**
     * Real 50-day SMA over closes, computed client-side from historical_prices
     * (fetched as a full year regardless of the selected range so there's
     * enough lookback to seed the average) — not a fabricated decorative
     * line. Empty if there isn't a full year of history yet for this ticker.
     */
    val sma50: List<Double?>
        get() {
            val closes = fullHistory.mapNotNull { it.close }
            if (closes.size < SMA_PERIOD) return emptyList()
            val fullSma = closes.indices.map { i ->
                if (i < SMA_PERIOD - 1) null
                else closes.subList(i - SMA_PERIOD + 1, i + 1).average()
            }
            return fullSma.takeLast(visibleDayCount)
        }
}

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
            val historyResult = repository.getTickerHistory(ticker, "1Y")
            val newsResult = repository.getTickerNews(ticker)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = detailResult.exceptionOrNull()?.message,
                detail = detailResult.getOrNull(),
                fullHistory = historyResult.getOrDefault(emptyList()).filter { it.close != null },
                news = newsResult.getOrDefault(emptyList()),
            )
        }
    }

    /** Just re-slices the already-fetched full year — no network call needed. */
    fun setRange(range: String) {
        _uiState.value = _uiState.value.copy(range = range)
    }

    fun toggleSignals() {
        _uiState.value = _uiState.value.copy(signalsOpen = !_uiState.value.signalsOpen)
    }
}
