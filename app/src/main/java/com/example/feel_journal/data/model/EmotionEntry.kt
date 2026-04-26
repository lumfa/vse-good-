package com.example.feel_journal.data.model

import com.google.gson.annotations.SerializedName

data class EmotionEntry(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("note") val note: String?,
    @SerializedName("userId") val userId: Int,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("emotionId") val emotionId: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("user") val user: User? = null,
    @SerializedName("category") val category: Category? = null,
    @SerializedName("emotion") val emotion: Emotion? = null
)
