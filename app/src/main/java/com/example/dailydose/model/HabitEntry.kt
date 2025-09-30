package com.example.dailydose.model

import java.util.Date

data class HabitEntry(
    val id: String = "",
    val title: String,
    val description: String,
    val category: String = "General",
    val date: Date = Date(),
    val timestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = true,
    val notes: String = ""
)

enum class HabitCategory(val displayName: String, val icon: String, val color: String) {
    HEALTH("Health & Wellness", "💊", "#4CAF50"),
    FITNESS("Fitness", "🏃", "#2196F3"),
    PRODUCTIVITY("Productivity", "📚", "#FF9800"),
    MINDFULNESS("Mindfulness", "🧘", "#9C27B0"),
    SOCIAL("Social", "👥", "#E91E63"),
    LEARNING("Learning", "🎓", "#607D8B"),
    CREATIVITY("Creativity", "🎨", "#795548"),
    GENERAL("General", "⭐", "#9E9E9E")
}
