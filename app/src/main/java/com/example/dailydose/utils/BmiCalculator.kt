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
            bmi < 25.0 -> BMICategory.NORMAL_WEIGHT
            bmi < 30.0 -> BMICategory.OVERWEIGHT
            bmi < 35.0 -> BMICategory.OBESITY_CLASS_I
            bmi < 40.0 -> BMICategory.OBESITY_CLASS_II
            else -> BMICategory.OBESITY_CLASS_III
        }
    }
    
    fun getBMIColor(bmi: Double): String {
        return when (getBMICategory(bmi)) {
            BMICategory.UNDERWEIGHT -> "#2196F3" // Blue
            BMICategory.NORMAL_WEIGHT -> "#4CAF50" // Green
            BMICategory.OVERWEIGHT -> "#FF9800" // Orange
            BMICategory.OBESITY_CLASS_I -> "#FF5722" // Deep Orange
            BMICategory.OBESITY_CLASS_II -> "#F44336" // Red
            BMICategory.OBESITY_CLASS_III -> "#9C27B0" // Purple
        }
    }
    
    fun getBMIAdvice(bmi: Double): String {
        return when (getBMICategory(bmi)) {
            BMICategory.UNDERWEIGHT -> "You are underweight. Consider consulting a healthcare provider for healthy weight gain strategies and nutritional guidance."
            BMICategory.NORMAL_WEIGHT -> "Excellent! You are in a healthy weight range. Keep up the good work with balanced nutrition and regular exercise."
            BMICategory.OVERWEIGHT -> "You are overweight. Consider a balanced diet and regular exercise to reach a healthier weight. Consult a healthcare provider for guidance."
            BMICategory.OBESITY_CLASS_I -> "You are in Obesity Class I. It's important to consult a healthcare provider for a personalized weight management plan."
            BMICategory.OBESITY_CLASS_II -> "You are in Obesity Class II. Please seek immediate medical advice for a comprehensive weight management program."
            BMICategory.OBESITY_CLASS_III -> "You are in Obesity Class III (Severe Obesity). Please consult a healthcare provider immediately for specialized medical care and treatment options."
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
    NORMAL_WEIGHT("Normal weight"),
    OVERWEIGHT("Overweight"),
    OBESITY_CLASS_I("Obesity (Class I)"),
    OBESITY_CLASS_II("Obesity (Class II)"),
    OBESITY_CLASS_III("Obesity (Class III / Severe obesity)")
}

