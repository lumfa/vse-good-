package com.example.feel_journal.data.remote.api

import com.example.feel_journal.data.remote.dto.EmotionDto
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface EmotionApi {

    @GET("api/emotions")
    fun getEmotions(): Observable<List<EmotionDto>>

    @GET("api/emotions/{id}")
    fun getEmotionById(@Path("id") id: Int): Observable<EmotionDto>
}
