package com.example.feel_journal.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MoodAnalysisDto(
    @SerializedName("emotionFrequencies") val emotionFrequencies: List<EmotionFrequencyDto>,
    @SerializedName("categoryMoods") val categoryMoods: List<CategoryMoodDto>,
    @SerializedName("emotionByCategory") val emotionByCategory: Map<String, Int>
)

data class EmotionFrequencyDto(
    @SerializedName("emotionName") val emotionName: String,
    @SerializedName("emotionIcon") val emotionIcon: String,
    @SerializedName("count") val count: Int,
    @SerializedName("percentage") val percentage: Double
)

data class CategoryMoodDto(
    @SerializedName("categoryName") val categoryName: String,
    @SerializedName("categoryType") val categoryType: String,
    @SerializedName("dominantEmotion") val dominantEmotion: String,
    @SerializedName("dominantEmotionIcon") val dominantEmotionIcon: String,
    @SerializedName("dominantEmotionCount") val dominantEmotionCount: Int,
    @SerializedName("totalEntries") val totalEntries: Int
)

data class ExportRequest(
    @SerializedName("format") val format: String = "CSV",
    @SerializedName("dateFrom") val dateFrom: String?,
    @SerializedName("dateTo") val dateTo: String?
)
