package com.example.dailydose.model

data class HealthGoal(
    val id: String = "",
    val type: HealthType,
    val targetValue: Double,
    val currentValue: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 days
    val isActive: Boolean = true
)

