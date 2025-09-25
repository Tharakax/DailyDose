package com.example.dailydose.model

import java.util.*

data class Reminder(
    val id: String = "",
    val title: String,
    val message: String,
    val reminderTime: Date,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val category: String = "Health"
)
