package com.example.dailydose.model

import java.util.Date

data class HealthEntry(
    val id: String = "",
    val type: HealthType,
    val value: Double,
    val unit: String,
    val date: Date = Date(),
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

enum class HealthType(val displayName: String, val unit: String, val icon: String) {
    WEIGHT("Weight", "kg", "⚖️"),
    HEIGHT("Height", "cm", "📏"),
    BLOOD_PRESSURE("Blood Pressure", "mmHg", "🩸"),
    HEART_RATE("Heart Rate", "bpm", "❤️"),
    BLOOD_SUGAR("Blood Sugar", "mg/dL", "🍯"),
    TEMPERATURE("Temperature", "°C", "🌡️"),
    SLEEP("Sleep", "hours", "😴"),
    STEPS("Steps", "count", "👟"),
    WATER("Water Intake", "L", "💧"),
    EXERCISE("Exercise", "minutes", "🏃")
}

