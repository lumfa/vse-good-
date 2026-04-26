package com.example.feel_journal.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feel_journal.data.remote.ApiClient
import com.example.feel_journal.data.remote.dto.*
import com.example.feel_journal.ui.state.AuthUiState
import com.example.feel_journal.ui.state.RecoveryUiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    private val api = ApiClient.authApi
    private val disposables = CompositeDisposable()

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    private val _recoveryState = MutableStateFlow<RecoveryUiState>(RecoveryUiState.Idle)
    val recoveryState: StateFlow<RecoveryUiState> = _recoveryState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserDto?>(null)
    val currentUser: StateFlow<UserDto?> = _currentUser.asStateFlow()

    fun register(email: String, password: String, name: String) {
        _authState.value = AuthUiState.Loading
        disposables.add(
            api.register(RegisterRequest(email, password, name))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ApiClient.setToken(response.token)
                    _currentUser.value = response.user
                    _authState.value = AuthUiState.Success(response.user, response.token)
                }, { error ->
                    _authState.value = AuthUiState.Error(
                        error.message ?: "Registration failed"
                    )
                })
        )
    }

    fun login(email: String, password: String) {
        _authState.value = AuthUiState.Loading
        disposables.add(
            api.login(LoginRequest(email, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    ApiClient.setToken(response.token)
                    _currentUser.value = response.user
                    _authState.value = AuthUiState.Success(response.user, response.token)
                }, { error ->
                    _authState.value = AuthUiState.Error(
                        error.message ?: "Login failed"
                    )
                })
        )
    }

    fun initiateRecovery(email: String) {
        _recoveryState.value = RecoveryUiState.Loading
        disposables.add(
            api.initiateRecovery(RecoveryRequest(email))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _recoveryState.value = RecoveryUiState.Sent
                }, { error ->
                    _recoveryState.value = RecoveryUiState.Error(
                        error.message ?: "Recovery failed"
                    )
                })
        )
    }

    fun resetPassword(email: String, newPassword: String, token: String) {
        _authState.value = AuthUiState.Loading
        disposables.add(
            api.resetPassword(ResetPasswordRequest(email, newPassword, token))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _authState.value = AuthUiState.Idle
                }, { error ->
                    _authState.value = AuthUiState.Error(
                        error.message ?: "Reset failed"
                    )
                })
        )
    }

    fun loadCurrentUser() {
        disposables.add(
            api.getMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ user ->
                    _currentUser.value = user
                }, { })
        )
    }

    fun logout() {
        ApiClient.setToken(null)
        _currentUser.value = null
        _authState.value = AuthUiState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthUiState.Idle
    }

    fun resetRecoveryState() {
        _recoveryState.value = RecoveryUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
