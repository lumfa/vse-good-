package com.example.feel_journal.ui.state

import com.example.feel_journal.data.remote.dto.EmotionEntryDto

data class EntriesUiState(
    val entries: List<EmotionEntryDto> = emptyList(),
    val page: Int = 1,
    val totalPages: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasNext: Boolean = false,
    val error: String? = null
)

sealed interface EntryDetailUiState {
    data object Idle : EntryDetailUiState
    data object Loading : EntryDetailUiState
    data class Success(val entry: EmotionEntryDto) : EntryDetailUiState
    data class Error(val message: String) : EntryDetailUiState
}

sealed interface EntryActionUiState {
    data object Idle : EntryActionUiState
    data object Loading : EntryActionUiState
    data class Created(val entry: EmotionEntryDto) : EntryActionUiState
    data class Updated(val entry: EmotionEntryDto) : EntryActionUiState
    data object Deleted : EntryActionUiState
    data class Error(val message: String) : EntryActionUiState
}
