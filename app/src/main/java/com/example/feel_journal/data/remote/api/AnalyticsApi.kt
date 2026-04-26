package com.example.feel_journal.data.remote.api

import com.example.feel_journal.data.remote.dto.MoodAnalysisDto
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface AnalyticsApi {

    @GET("api/analytics/mood")
    fun analyzeMood(
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null
    ): Observable<MoodAnalysisDto>

    @GET("api/analytics/export")
    fun export(
        @Query("format") format: String = "CSV",
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null
    ): Observable<ResponseBody>
}
