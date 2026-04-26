package com.example.feel_journal.ui.viewmodel

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.example.feel_journal.data.remote.ApiClient
import com.example.feel_journal.data.remote.dto.MoodAnalysisDto
import com.example.feel_journal.ui.state.AnalyticsUiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.ResponseBody
import java.io.OutputStream

class AnalyticsViewModel : ViewModel() {

    private val api = ApiClient.analyticsApi
    private val disposables = CompositeDisposable()

    private val _analyticsState = MutableStateFlow(AnalyticsUiState())
    val analyticsState: StateFlow<AnalyticsUiState> = _analyticsState.asStateFlow()

    fun analyzeMood(dateFrom: String? = null, dateTo: String? = null) {
        _analyticsState.value = _analyticsState.value.copy(isLoading = true, error = null)
        disposables.add(
            api.analyzeMood(dateFrom, dateTo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ analysis ->
                    _analyticsState.value = AnalyticsUiState(
                        analysis = analysis, isLoading = false
                    )
                }, { error ->
                    _analyticsState.value = _analyticsState.value.copy(
                        isLoading = false, error = error.message ?: "Analysis failed"
                    )
                })
        )
    }

    fun exportCsv(context: Context, dateFrom: String? = null, dateTo: String? = null) {
        _analyticsState.value = _analyticsState.value.copy(isExporting = true, error = null)
        disposables.add(
            api.export(format = "CSV", dateFrom = dateFrom, dateTo = dateTo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ body ->
                    saveFile(context, body, "feel_journal_export.csv", "text/csv")
                    _analyticsState.value = _analyticsState.value.copy(isExporting = false)
                }, { error ->
                    _analyticsState.value = _analyticsState.value.copy(
                        isExporting = false, error = error.message ?: "Export failed"
                    )
                })
        )
    }

    fun exportPdf(context: Context, dateFrom: String? = null, dateTo: String? = null) {
        _analyticsState.value = _analyticsState.value.copy(isExporting = true, error = null)
        disposables.add(
            api.export(format = "PDF", dateFrom = dateFrom, dateTo = dateTo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ body ->
                    saveFile(context, body, "feel_journal_export.pdf", "application/pdf")
                    _analyticsState.value = _analyticsState.value.copy(isExporting = false)
                }, { error ->
                    _analyticsState.value = _analyticsState.value.copy(
                        isExporting = false, error = error.message ?: "Export failed"
                    )
                })
        )
    }

    private fun saveFile(context: Context, body: ResponseBody, fileName: String, mimeType: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return

        val inputStream = body.byteStream()
        val outputStream: OutputStream? = resolver.openOutputStream(uri)

        outputStream?.use { os ->
            inputStream.use { `is` ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (`is`.read(buffer).also { bytesRead = it } != -1) {
                    os.write(buffer, 0, bytesRead)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
