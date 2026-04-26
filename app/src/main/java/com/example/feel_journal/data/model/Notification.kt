package com.example.feel_journal.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id") val id: Int,
    @SerializedName("message") val message: String,
    @SerializedName("date") val date: String,
    @SerializedName("userId") val userId: Int,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("createdAt") val createdAt: String
)
