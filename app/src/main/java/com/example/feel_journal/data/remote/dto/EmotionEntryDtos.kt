package com.example.feel_journal.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateEmotionEntryRequest(
    @SerializedName("date") val date: String,
    @SerializedName("note") val note: String?,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("emotionId") val emotionId: Int
)

data class UpdateEmotionEntryRequest(
    @SerializedName("date") val date: String,
    @SerializedName("note") val note: String?,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("emotionId") val emotionId: Int
)

data class EmotionEntryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("note") val note: String?,
    @SerializedName("userId") val userId: Int,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("emotionId") val emotionId: Int,
    @SerializedName("category") val category: CategoryDto? = null,
    @SerializedName("emotion") val emotion: EmotionDto? = null,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String
)

data class EmotionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String
)

data class PagedResponse<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("hasPrev") val hasPrev: Boolean
)
