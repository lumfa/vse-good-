package com.example.feel_journal.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feel_journal.data.remote.ApiClient
import com.example.feel_journal.data.remote.dto.*
import com.example.feel_journal.ui.state.CategoriesUiState
import com.example.feel_journal.ui.state.CategoryActionUiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoriesViewModel : ViewModel() {

    private val api = ApiClient.categoryApi
    private val disposables = CompositeDisposable()

    private val _categoriesState = MutableStateFlow(CategoriesUiState())
    val categoriesState: StateFlow<CategoriesUiState> = _categoriesState.asStateFlow()

    private val _actionState = MutableStateFlow(CategoryActionUiState.Idle)
    val actionState: StateFlow<CategoryActionUiState> = _actionState.asStateFlow()

    fun loadCategories() {
        _categoriesState.value = _categoriesState.value.copy(isLoading = true, error = null)
        disposables.add(
            api.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ categories ->
                    _categoriesState.value = CategoriesUiState(
                        categories = categories, isLoading = false
                    )
                }, { error ->
                    _categoriesState.value = _categoriesState.value.copy(
                        isLoading = false, error = error.message ?: "Failed to load categories"
                    )
                })
        )
    }

    fun createCategory(name: String, type: String) {
        _actionState.value = CategoryActionUiState.Loading
        disposables.add(
            api.createCategory(CreateCategoryRequest(name, type))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ category ->
                    _actionState.value = CategoryActionUiState.Success(category)
                    loadCategories()
                }, { error ->
                    _actionState.value = CategoryActionUiState.Error(
                        error.message ?: "Failed to create category"
                    )
                })
        )
    }

    fun updateCategory(id: Int, name: String, type: String) {
        _actionState.value = CategoryActionUiState.Loading
        disposables.add(
            api.updateCategory(id, UpdateCategoryRequest(name, type))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ category ->
                    _actionState.value = CategoryActionUiState.Success(category)
                    loadCategories()
                }, { error ->
                    _actionState.value = CategoryActionUiState.Error(
                        error.message ?: "Failed to update category"
                    )
                })
        )
    }

    fun deleteCategory(id: Int) {
        _actionState.value = CategoryActionUiState.Loading
        disposables.add(
            api.deleteCategory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _actionState.value = CategoryActionUiState.Deleted
                    loadCategories()
                }, { error ->
                    _actionState.value = CategoryActionUiState.Error(
                        error.message ?: "Failed to delete category"
                    )
                })
        )
    }

    fun resetActionState() {
        _actionState.value = CategoryActionUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
