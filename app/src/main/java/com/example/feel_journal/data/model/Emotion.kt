package com.example.feel_journal.data.model

import com.google.gson.annotations.SerializedName

data class Emotion(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("createdAt") val createdAt: String
)
