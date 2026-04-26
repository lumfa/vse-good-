package com.example.feel_journal.ui.state

import com.example.feel_journal.data.remote.dto.CategoryDto

data class CategoriesUiState(
    val categories: List<CategoryDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface CategoryActionUiState {
    data object Idle : CategoryActionUiState
    data object Loading : CategoryActionUiState
    data class Success(val category: CategoryDto) : CategoryActionUiState
    data object Deleted : CategoryActionUiState
    data class Error(val message: String) : CategoryActionUiState
}
