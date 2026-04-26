package com.example.feel_journal.ui.state

import com.example.feel_journal.data.remote.dto.UserDto

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: UserDto, val token: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

sealed interface RecoveryUiState {
    data object Idle : RecoveryUiState
    data object Loading : RecoveryUiState
    data object Sent : RecoveryUiState
    data class Error(val message: String) : RecoveryUiState
}
