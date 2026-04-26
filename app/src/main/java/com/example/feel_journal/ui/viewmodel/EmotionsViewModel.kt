package com.example.feel_journal.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feel_journal.data.remote.ApiClient
import com.example.feel_journal.data.remote.dto.EmotionDto
import com.example.feel_journal.ui.state.EmotionsUiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmotionsViewModel : ViewModel() {

    private val api = ApiClient.emotionApi
    private val disposables = CompositeDisposable()

    private val _emotionsState = MutableStateFlow(EmotionsUiState())
    val emotionsState: StateFlow<EmotionsUiState> = _emotionsState.asStateFlow()

    fun loadEmotions() {
        _emotionsState.value = _emotionsState.value.copy(isLoading = true, error = null)
        disposables.add(
            api.getEmotions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ emotions ->
                    _emotionsState.value = EmotionsUiState(
                        emotions = emotions, isLoading = false
                    )
                }, { error ->
                    _emotionsState.value = _emotionsState.value.copy(
                        isLoading = false, error = error.message ?: "Failed to load emotions"
                    )
                })
        )
    }

    fun getEmotionById(id: Int): EmotionDto? {
        return _emotionsState.value.emotions.find { it.id == id }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
