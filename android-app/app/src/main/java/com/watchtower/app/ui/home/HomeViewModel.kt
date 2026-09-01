package com.watchtower.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchtower.app.data.WatchtowerRepository
import com.watchtower.app.data.favorites.FavoritesRepository
import com.watchtower.app.data.model.DigestSummary
import com.watchtower.app.data.model.TickerSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SortKey { SYMBOL, PRICE, CHANGE, SCORE }

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val tickers: List<TickerSummary> = emptyList(),
    val latestDigest: DigestSummary? = null,
    val sortKey: SortKey = SortKey.SCORE,
    val sortAscending: Boolean = false,
    val filterIndustry: String? = null,
    val searchActive: Boolean = false,
    val searchQuery: String = "",
    val favoriteTickers: Set<String> = emptySet(),
) {
    val industries: List<String>
        // A couple of rows in the source DB have the literal string "null"
        // instead of a real SQL NULL — filter it out so it never shows up
        // as a selectable filter chip.
        get() = tickers.mapNotNull { it.industry }
            .filterNot { it.equals("null", ignoreCase = true) }
            .distinct().sorted()

    val movers: List<TickerSummary>
        get() = tickers.sortedByDescending { row -> row.changePct?.let { kotlin.math.abs(it) } ?: 0.0 }.take(4)

    val favorites: List<TickerSummary>
        get() = tickers.filter { it.ticker in favoriteTickers }

    val rows: List<TickerSummary>
        get() {
            val byIndustry = if (filterIndustry == null) tickers else tickers.filter { it.industry == filterIndustry }
            val query = searchQuery.trim()
            val filtered = if (query.isEmpty()) {
                byIndustry
            } else {
                byIndustry.filter {
                    it.ticker.contains(query, ignoreCase = true) ||
                        it.name?.contains(query, ignoreCase = true) == true
                }
            }
            val sorted = when (sortKey) {
                SortKey.SYMBOL -> filtered.sortedBy { it.ticker }
                SortKey.PRICE -> filtered.sortedBy { it.price ?: Double.NEGATIVE_INFINITY }
                SortKey.CHANGE -> filtered.sortedBy { it.changePct ?: Double.NEGATIVE_INFINITY }
                SortKey.SCORE -> filtered.sortedBy { it.compositeScore ?: Double.NEGATIVE_INFINITY }
            }
            return if (sortAscending) sorted else sorted.reversed()
        }
}

class HomeViewModel(
    private val repository: WatchtowerRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
        viewModelScope.launch {
            favoritesRepository.favoritesFlow.collect { favorites ->
                _uiState.value = _uiState.value.copy(favoriteTickers = favorites)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val previous = _uiState.value
            _uiState.value = previous.copy(isLoading = true, error = null)

            val tickersResult = repository.getTickers()
            val digestResult = repository.getDigests(page = 0, size = 1)

            // A failed pull-to-refresh keeps showing the last good data
            // (with an error message) rather than blanking the screen —
            // previous.tickers/latestDigest, not emptyList()/null.
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = tickersResult.exceptionOrNull()?.message,
                tickers = tickersResult.getOrDefault(previous.tickers),
                latestDigest = digestResult.getOrNull()?.content?.firstOrNull() ?: previous.latestDigest,
            )
        }
    }

    fun setSort(key: SortKey) {
        val current = _uiState.value
        _uiState.value = if (current.sortKey == key) {
            current.copy(sortAscending = !current.sortAscending)
        } else {
            current.copy(sortKey = key, sortAscending = key == SortKey.SYMBOL)
        }
    }

    fun setFilter(industry: String?) {
        _uiState.value = _uiState.value.copy(filterIndustry = industry)
    }

    fun openSearch() {
        _uiState.value = _uiState.value.copy(searchActive = true)
    }

    fun closeSearch() {
        _uiState.value = _uiState.value.copy(searchActive = false, searchQuery = "")
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleFavorite(ticker: String) {
        viewModelScope.launch {
            favoritesRepository.toggle(ticker)
        }
    }
}
