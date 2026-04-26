package com.example.feel_journal.ui.state

import com.example.feel_journal.data.remote.dto.EmotionDto

data class EmotionsUiState(
    val emotions: List<EmotionDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
