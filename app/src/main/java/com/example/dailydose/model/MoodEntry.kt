package com.example.dailydose.model

import java.util.Date

data class MoodEntry(
    val id: String = "",
    val mood: MoodType,
    val intensity: Int = 5, // 1-10 scale
    val notes: String = "",
    val date: Date = Date(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class MoodType(val displayName: String, val emoji: String, val color: String, val themeColors: MoodTheme) {
    VERY_HAPPY("Very Happy", "😄", "#4CAF50", MoodTheme(
        primary = "#4CAF50",
        primaryLight = "#81C784",
        primaryDark = "#388E3C",
        accent = "#8BC34A",
        background = "#E8F5E8",
        surface = "#FFFFFF",
        textPrimary = "#2E7D32",
        textSecondary = "#66BB6A"
    )),
    HAPPY("Happy", "😊", "#8BC34A", MoodTheme(
        primary = "#8BC34A",
        primaryLight = "#AED581",
        primaryDark = "#689F38",
        accent = "#CDDC39",
        background = "#F1F8E9",
        surface = "#FFFFFF",
        textPrimary = "#558B2F",
        textSecondary = "#9CCC65"
    )),
    NEUTRAL("Neutral", "😐", "#FFC107", MoodTheme(
        primary = "#FFC107",
        primaryLight = "#FFD54F",
        primaryDark = "#F57F17",
        accent = "#FFEB3B",
        background = "#FFFDE7",
        surface = "#FFFFFF",
        textPrimary = "#F57F17",
        textSecondary = "#FFB300"
    )),
    SAD("Sad", "😢", "#FF9800", MoodTheme(
        primary = "#FF9800",
        primaryLight = "#FFB74D",
        primaryDark = "#F57C00",
        accent = "#FF5722",
        background = "#FFF3E0",
        surface = "#FFFFFF",
        textPrimary = "#E65100",
        textSecondary = "#FF9800"
    )),
    VERY_SAD("Very Sad", "😭", "#F44336", MoodTheme(
        primary = "#F44336",
        primaryLight = "#EF5350",
        primaryDark = "#D32F2F",
        accent = "#E91E63",
        background = "#FFEBEE",
        surface = "#FFFFFF",
        textPrimary = "#C62828",
        textSecondary = "#EF5350"
    )),
    ANGRY("Angry", "😠", "#E91E63", MoodTheme(
        primary = "#E91E63",
        primaryLight = "#F06292",
        primaryDark = "#C2185B",
        accent = "#F44336",
        background = "#FCE4EC",
        surface = "#FFFFFF",
        textPrimary = "#AD1457",
        textSecondary = "#F06292"
    )),
    ANXIOUS("Anxious", "😰", "#9C27B0", MoodTheme(
        primary = "#9C27B0",
        primaryLight = "#BA68C8",
        primaryDark = "#7B1FA2",
        accent = "#673AB7",
        background = "#F3E5F5",
        surface = "#FFFFFF",
        textPrimary = "#6A1B9A",
        textSecondary = "#AB47BC"
    )),
    EXCITED("Excited", "🤩", "#00BCD4", MoodTheme(
        primary = "#00BCD4",
        primaryLight = "#4DD0E1",
        primaryDark = "#0097A7",
        accent = "#00E5FF",
        background = "#E0F2F1",
        surface = "#FFFFFF",
        textPrimary = "#00695C",
        textSecondary = "#26C6DA"
    )),
    CALM("Calm", "😌", "#607D8B", MoodTheme(
        primary = "#607D8B",
        primaryLight = "#90A4AE",
        primaryDark = "#455A64",
        accent = "#78909C",
        background = "#ECEFF1",
        surface = "#FFFFFF",
        textPrimary = "#37474F",
        textSecondary = "#78909C"
    )),
    TIRED("Tired", "😴", "#795548", MoodTheme(
        primary = "#795548",
        primaryLight = "#A1887F",
        primaryDark = "#5D4037",
        accent = "#8D6E63",
        background = "#EFEBE9",
        surface = "#FFFFFF",
        textPrimary = "#3E2723",
        textSecondary = "#8D6E63"
    ))
}

data class MoodTheme(
    val primary: String,
    val primaryLight: String,
    val primaryDark: String,
    val accent: String,
    val background: String,
    val surface: String,
    val textPrimary: String,
    val textSecondary: String
)
