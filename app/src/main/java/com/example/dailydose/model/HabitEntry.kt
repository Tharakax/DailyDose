package com.example.dailydose.model

import com.example.dailydose.R
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

enum class HabitCategory(val displayName: String, val iconResId: Int, val color: String) {
    SLEEP("Sleep", R.drawable.ic_sleep, "#673AB7"),
    WALK("Walk", R.drawable.ic_walk, "#4CAF50"),
    STUDY("Study", R.drawable.ic_study, "#FF9800"),
    MEDITATION("Meditation", R.drawable.ic_meditation, "#9C27B0"),
    WORKOUT("Workout", R.drawable.ic_workout, "#F44336"),
    TRAVEL("Travel", R.drawable.ic_travel, "#2196F3"),
    EATING("Eating", R.drawable.ic_eating, "#FF5722"),
    GENERAL("General", R.drawable.ic_star, "#9E9E9E")
}
