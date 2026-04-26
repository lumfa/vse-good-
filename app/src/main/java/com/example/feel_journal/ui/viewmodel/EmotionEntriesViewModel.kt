package com.example.feel_journal.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feel_journal.data.remote.ApiClient
import com.example.feel_journal.data.remote.dto.*
import com.example.feel_journal.ui.state.EntriesUiState
import com.example.feel_journal.ui.state.EntryActionUiState
import com.example.feel_journal.ui.state.EntryDetailUiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmotionEntriesViewModel : ViewModel() {

    private val api = ApiClient.emotionEntryApi
    private val disposables = CompositeDisposable()

    private val _entriesState = MutableStateFlow(EntriesUiState())
    val entriesState: StateFlow<EntriesUiState> = _entriesState.asStateFlow()

    private val _detailState = MutableStateFlow<EntryDetailUiState>(EntryDetailUiState.Idle)
    val detailState: StateFlow<EntryDetailUiState> = _detailState.asStateFlow()

    private val _actionState = MutableStateFlow<EntryActionUiState>(EntryActionUiState.Idle)
    val actionState: StateFlow<EntryActionUiState> = _actionState.asStateFlow()

    private var currentFilters = EntryFilters()

    private data class EntryFilters(
        val dateFrom: String? = null,
        val dateTo: String? = null,
        val categoryId: Int? = null,
        val emotionId: Int? = null
    )

    fun loadEntries(refresh: Boolean = true) {
        if (refresh) {
            _entriesState.value = _entriesState.value.copy(
                isLoading = true, page = 1, entries = emptyList(), error = null
            )
        }

        disposables.add(
            api.getEntries(
                page = if (refresh) 1 else _entriesState.value.page,
                pageSize = 20,
                dateFrom = currentFilters.dateFrom,
                dateTo = currentFilters.dateTo,
                categoryId = currentFilters.categoryId,
                emotionId = currentFilters.emotionId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ paged ->
                    val entries = if (refresh) paged.items
                    else _entriesState.value.entries + paged.items

                    _entriesState.value = EntriesUiState(
                        entries = entries,
                        page = paged.page,
                        totalPages = paged.totalPages,
                        totalCount = paged.totalCount,
                        hasNext = paged.hasNext,
                        isLoading = false,
                        isLoadingMore = false,
                        isRefreshing = false
                    )
                }, { error ->
                    _entriesState.value = _entriesState.value.copy(
                        isLoading = false, isLoadingMore = false, isRefreshing = false,
                        error = error.message ?: "Failed to load entries"
                    )
                })
        )
    }

    fun loadMore() {
        val state = _entriesState.value
        if (!state.hasNext || state.isLoadingMore) return

        _entriesState.value = state.copy(isLoadingMore = true)

        val nextPage = state.page + 1
        disposables.add(
            api.getEntries(
                page = nextPage,
                pageSize = 20,
                dateFrom = currentFilters.dateFrom,
                dateTo = currentFilters.dateTo,
                categoryId = currentFilters.categoryId,
                emotionId = currentFilters.emotionId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ paged ->
                    _entriesState.value = _entriesState.value.copy(
                        entries = _entriesState.value.entries + paged.items,
                        page = paged.page,
                        totalPages = paged.totalPages,
                        totalCount = paged.totalCount,
                        hasNext = paged.hasNext,
                        isLoadingMore = false
                    )
                }, { error ->
                    _entriesState.value = _entriesState.value.copy(
                        isLoadingMore = false,
                        error = error.message ?: "Failed to load more"
                    )
                })
        )
    }

    fun setFilters(dateFrom: String? = null, dateTo: String? = null,
                   categoryId: Int? = null, emotionId: Int? = null) {
        currentFilters = EntryFilters(dateFrom, dateTo, categoryId, emotionId)
        loadEntries(refresh = true)
    }

    fun refresh() {
        _entriesState.value = _entriesState.value.copy(isRefreshing = true)
        loadEntries(refresh = true)
    }

    fun loadEntryById(id: Int) {
        _detailState.value = EntryDetailUiState.Loading
        disposables.add(
            api.getEntryById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ entry ->
                    _detailState.value = EntryDetailUiState.Success(entry)
                }, { error ->
                    _detailState.value = EntryDetailUiState.Error(
                        error.message ?: "Failed to load entry"
                    )
                })
        )
    }

    fun createEntry(date: String, note: String?, categoryId: Int, emotionId: Int) {
        _actionState.value = EntryActionUiState.Loading
        disposables.add(
            api.createEntry(CreateEmotionEntryRequest(date, note, categoryId, emotionId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ entry ->
                    _actionState.value = EntryActionUiState.Created(entry)
                    loadEntries(refresh = true)
                }, { error ->
                    _actionState.value = EntryActionUiState.Error(
                        error.message ?: "Failed to create entry"
                    )
                })
        )
    }

    fun updateEntry(id: Int, date: String, note: String?, categoryId: Int, emotionId: Int) {
        _actionState.value = EntryActionUiState.Loading
        disposables.add(
            api.updateEntry(id, UpdateEmotionEntryRequest(date, note, categoryId, emotionId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ entry ->
                    _actionState.value = EntryActionUiState.Updated(entry)
                    loadEntries(refresh = true)
                }, { error ->
                    _actionState.value = EntryActionUiState.Error(
                        error.message ?: "Failed to update entry"
                    )
                })
        )
    }

    fun deleteEntry(id: Int) {
        _actionState.value = EntryActionUiState.Loading
        disposables.add(
            api.deleteEntry(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _actionState.value = EntryActionUiState.Deleted
                    loadEntries(refresh = true)
                }, { error ->
                    _actionState.value = EntryActionUiState.Error(
                        error.message ?: "Failed to delete entry"
                    )
                })
        )
    }

    fun resetActionState() {
        _actionState.value = EntryActionUiState.Idle
    }

    fun resetDetailState() {
        _detailState.value = EntryDetailUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
