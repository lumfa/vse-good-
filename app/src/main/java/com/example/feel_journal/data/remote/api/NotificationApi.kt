package com.example.feel_journal.data.remote.api

import com.example.feel_journal.data.remote.dto.CreateNotificationRequest
import com.example.feel_journal.data.remote.dto.NotificationDto
import com.example.feel_journal.data.remote.dto.UpdateNotificationRequest
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface NotificationApi {

    @GET("api/notifications")
    fun getNotifications(
        @Query("unreadOnly") unreadOnly: Boolean? = null
    ): Observable<List<NotificationDto>>

    @POST("api/notifications")
    fun createNotification(@Body request: CreateNotificationRequest): Observable<NotificationDto>

    @PUT("api/notifications/{id}")
    fun updateNotification(
        @Path("id") id: Int,
        @Body request: UpdateNotificationRequest
    ): Observable<NotificationDto>

    @PATCH("api/notifications/{id}/read")
    fun markAsRead(@Path("id") id: Int): Observable<Unit>

    @DELETE("api/notifications/{id}")
    fun deleteNotification(@Path("id") id: Int): Observable<Unit>
}
