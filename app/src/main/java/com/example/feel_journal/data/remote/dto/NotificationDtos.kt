package com.example.feel_journal.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateNotificationRequest(
    @SerializedName("message") val message: String,
    @SerializedName("date") val date: String
)

data class UpdateNotificationRequest(
    @SerializedName("message") val message: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("isRead") val isRead: Boolean?
)

data class NotificationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("message") val message: String,
    @SerializedName("date") val date: String,
    @SerializedName("userId") val userId: Int,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("createdAt") val createdAt: String
)
