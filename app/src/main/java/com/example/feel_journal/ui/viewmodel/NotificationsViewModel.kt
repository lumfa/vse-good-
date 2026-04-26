package com.example.feel_journal.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feel_journal.data.remote.ApiClient
import com.example.feel_journal.data.remote.dto.*
import com.example.feel_journal.ui.state.NotificationsUiState
import com.example.feel_journal.ui.state.NotificationActionUiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationsViewModel : ViewModel() {

    private val api = ApiClient.notificationApi
    private val disposables = CompositeDisposable()

    private val _notificationsState = MutableStateFlow(NotificationsUiState())
    val notificationsState: StateFlow<NotificationsUiState> = _notificationsState.asStateFlow()

    private val _actionState = MutableStateFlow(NotificationActionUiState.Idle)
    val actionState: StateFlow<NotificationActionUiState> = _actionState.asStateFlow()

    fun loadNotifications(unreadOnly: Boolean = false) {
        _notificationsState.value = _notificationsState.value.copy(isLoading = true, error = null)
        disposables.add(
            api.getNotifications(unreadOnly = unreadOnly)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ notifications ->
                    _notificationsState.value = NotificationsUiState(
                        notifications = notifications,
                        unreadCount = notifications.count { !it.isRead },
                        isLoading = false
                    )
                }, { error ->
                    _notificationsState.value = _notificationsState.value.copy(
                        isLoading = false, error = error.message ?: "Failed to load notifications"
                    )
                })
        )
    }

    fun createNotification(message: String, date: String) {
        _actionState.value = NotificationActionUiState.Loading
        disposables.add(
            api.createNotification(CreateNotificationRequest(message, date))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ notification ->
                    _actionState.value = NotificationActionUiState.Success(notification)
                    loadNotifications()
                }, { error ->
                    _actionState.value = NotificationActionUiState.Error(
                        error.message ?: "Failed to create notification"
                    )
                })
        )
    }

    fun updateNotification(id: Int, message: String?, date: String?, isRead: Boolean?) {
        _actionState.value = NotificationActionUiState.Loading
        disposables.add(
            api.updateNotification(id, UpdateNotificationRequest(message, date, isRead))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ notification ->
                    _actionState.value = NotificationActionUiState.Success(notification)
                    loadNotifications()
                }, { error ->
                    _actionState.value = NotificationActionUiState.Error(
                        error.message ?: "Failed to update notification"
                    )
                })
        )
    }

    fun markAsRead(id: Int) {
        disposables.add(
            api.markAsRead(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _actionState.value = NotificationActionUiState.MarkedAsRead
                    loadNotifications()
                }, { error ->
                    _actionState.value = NotificationActionUiState.Error(
                        error.message ?: "Failed to mark as read"
                    )
                })
        )
    }

    fun markAllAsRead() {
        val unreadIds = _notificationsState.value.notifications
            .filter { !it.isRead }
            .map { it.id }

        unreadIds.forEach { id -> markAsRead(id) }
    }

    fun deleteNotification(id: Int) {
        _actionState.value = NotificationActionUiState.Loading
        disposables.add(
            api.deleteNotification(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _actionState.value = NotificationActionUiState.Deleted
                    loadNotifications()
                }, { error ->
                    _actionState.value = NotificationActionUiState.Error(
                        error.message ?: "Failed to delete notification"
                    )
                })
        )
    }

    fun resetActionState() {
        _actionState.value = NotificationActionUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
