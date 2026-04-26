package com.example.feel_journal.data.remote.api

import com.example.feel_journal.data.remote.dto.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface EmotionEntryApi {

    @GET("api/entries")
    fun getEntries(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("emotionId") emotionId: Int? = null
    ): Observable<PagedResponse<EmotionEntryDto>>

    @GET("api/entries/{id}")
    fun getEntryById(@Path("id") id: Int): Observable<EmotionEntryDto>

    @POST("api/entries")
    fun createEntry(@Body request: CreateEmotionEntryRequest): Observable<EmotionEntryDto>

    @PUT("api/entries/{id}")
    fun updateEntry(
        @Path("id") id: Int,
        @Body request: UpdateEmotionEntryRequest
    ): Observable<EmotionEntryDto>

    @DELETE("api/entries/{id}")
    fun deleteEntry(@Path("id") id: Int): Observable<Unit>
}
