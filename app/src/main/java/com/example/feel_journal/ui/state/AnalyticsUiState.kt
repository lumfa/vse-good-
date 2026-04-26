package com.example.feel_journal.ui.state

import com.example.feel_journal.data.remote.dto.MoodAnalysisDto

data class AnalyticsUiState(
    val analysis: MoodAnalysisDto? = null,
    val isLoading: Boolean = false,
    val isExporting: Boolean = false,
    val error: String? = null
)
