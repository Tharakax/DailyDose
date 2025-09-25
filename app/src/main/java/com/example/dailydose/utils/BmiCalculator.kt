package com.example.dailydose.utils

import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthType
import kotlin.math.pow

object BmiCalculator {
    
    fun calculateBMI(weight: Double, height: Double): Double {
        if (height <= 0) return 0.0
        return weight / (height / 100).pow(2)
    }
    
    fun getBMICategory(bmi: Double): BMICategory {
        return when {
            bmi < 18.5 -> BMICategory.UNDERWEIGHT
            bmi < 25.0 -> BMICategory.NORMAL
            bmi < 30.0 -> BMICategory.OVERWEIGHT
            else -> BMICategory.OBESE
        }
    }
    
    fun getBMIColor(bmi: Double): String {
        return when (getBMICategory(bmi)) {
            BMICategory.UNDERWEIGHT -> "#2196F3" // Blue
            BMICategory.NORMAL -> "#4CAF50" // Green
            BMICategory.OVERWEIGHT -> "#FF9800" // Orange
            BMICategory.OBESE -> "#F44336" // Red
        }
    }
    
    fun getBMIAdvice(bmi: Double): String {
        return when (getBMICategory(bmi)) {
            BMICategory.UNDERWEIGHT -> "Consider consulting a healthcare provider for healthy weight gain strategies."
            BMICategory.NORMAL -> "Great! You're in a healthy weight range. Keep up the good work!"
            BMICategory.OVERWEIGHT -> "Consider a balanced diet and regular exercise to reach a healthier weight."
            BMICategory.OBESE -> "Please consult a healthcare provider for a personalized weight management plan."
        }
    }
    
    fun calculateBMIFromEntries(entries: List<HealthEntry>): Double? {
        val weightEntry = entries.find { it.type == HealthType.WEIGHT }
        val heightEntry = entries.find { it.type == HealthType.HEIGHT }
        
        if (weightEntry != null && heightEntry != null) {
            return calculateBMI(weightEntry.value, heightEntry.value)
        }
        return null
    }
}

enum class BMICategory(val displayName: String) {
    UNDERWEIGHT("Underweight"),
    NORMAL("Normal"),
    OVERWEIGHT("Overweight"),
    OBESE("Obese")
}
