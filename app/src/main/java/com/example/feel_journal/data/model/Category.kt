package com.example.feel_journal.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: CategoryType,
    @SerializedName("createdAt") val createdAt: String
)
