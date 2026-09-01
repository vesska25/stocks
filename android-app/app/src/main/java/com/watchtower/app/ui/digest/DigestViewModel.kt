package com.watchtower.app.ui.digest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchtower.app.data.WatchtowerRepository
import com.watchtower.app.data.model.DigestSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DigestUiState(
    val items: List<DigestSummary> = emptyList(),
    val expandedIds: Set<Long> = emptySet(),
    val page: Int = 0,
    val hasMore: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class DigestViewModel(private val repository: WatchtowerRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DigestUiState())
    val uiState: StateFlow<DigestUiState> = _uiState.asStateFlow()

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        val current = _uiState.value
        if (current.isLoading || !current.hasMore) return

        viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true, error = null)
            val result = repository.getDigests(page = current.page)
            result.fold(
                onSuccess = { page ->
                    _uiState.value = _uiState.value.copy(
                        items = _uiState.value.items + page.content,
                        page = current.page + 1,
                        hasMore = !page.last,
                        isLoading = false,
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                },
            )
        }
    }

    fun toggleExpanded(id: Long) {
        val expanded = _uiState.value.expandedIds
        _uiState.value = _uiState.value.copy(
            expandedIds = if (id in expanded) expanded - id else expanded + id,
        )
    }
}
