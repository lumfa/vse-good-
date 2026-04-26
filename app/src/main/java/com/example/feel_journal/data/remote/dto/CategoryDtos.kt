package com.example.feel_journal.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String = "NEUTRAL"
)

data class UpdateCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String = "NEUTRAL"
)
