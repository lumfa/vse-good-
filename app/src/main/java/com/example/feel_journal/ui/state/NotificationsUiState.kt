package com.example.feel_journal.ui.state

import com.example.feel_journal.data.remote.dto.NotificationDto

data class NotificationsUiState(
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface NotificationActionUiState {
    data object Idle : NotificationActionUiState
    data object Loading : NotificationActionUiState
    data class Success(val notification: NotificationDto) : NotificationActionUiState
    data object MarkedAsRead : NotificationActionUiState
    data object Deleted : NotificationActionUiState
    data class Error(val message: String) : NotificationActionUiState
}
