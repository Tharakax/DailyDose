package com.example.dailydose.model

import java.util.*

data class TodoItem(
    val id: String = "",
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val createdAt: Date = Date(),
    val dueDate: Date? = null,
    val category: String = "General"
)

enum class Priority(val displayName: String, val color: String) {
    LOW("Low", "#4CAF50"),
    MEDIUM("Medium", "#FF9800"),
    HIGH("High", "#F44336"),
    URGENT("Urgent", "#9C27B0")
}
