package com.example.feel_journal.data.model

enum class CategoryType(val value: String) {
    POSITIVE("POSITIVE"),
    NEGATIVE("NEGATIVE"),
    NEUTRAL("NEUTRAL");

    companion object {
        fun fromValue(value: String): CategoryType =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: NEUTRAL
    }
}
