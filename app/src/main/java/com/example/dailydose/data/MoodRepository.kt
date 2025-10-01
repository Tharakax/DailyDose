package com.example.dailydose.data

import android.content.Context
import com.example.dailydose.model.MoodEntry
import com.example.dailydose.model.MoodType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MoodRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("mood_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveMoodEntry(moodEntry: MoodEntry) {
        val entries = getAllMoodEntries().toMutableList()
        entries.add(moodEntry)
        saveMoodEntries(entries)
    }

    fun getAllMoodEntries(): List<MoodEntry> {
        val json = prefs.getString("mood_entries", "[]")
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun getTodayMoodEntries(): List<MoodEntry> {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        
        return getAllMoodEntries().filter { entry ->
            val entryDate = Calendar.getInstance().apply { time = entry.date }
            entryDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            entryDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        }
    }

    fun getMoodEntriesByDateRange(startDate: Date, endDate: Date): List<MoodEntry> {
        return getAllMoodEntries().filter { entry ->
            entry.date >= startDate && entry.date <= endDate
        }
    }

    fun getMoodStatistics(): Map<MoodType, Int> {
        val entries = getAllMoodEntries()
        return entries.groupingBy { it.mood }.eachCount()
    }

    fun getAverageMoodIntensity(): Double {
        val entries = getAllMoodEntries()
        return if (entries.isEmpty()) 0.0 else entries.map { it.intensity }.average()
    }

    fun getMoodTrend(days: Int = 7): List<Pair<Date, Double>> {
        val calendar = Calendar.getInstance()
        val trend = mutableListOf<Pair<Date, Double>>()
        
        repeat(days) { i ->
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val dayStart = calendar.clone() as Calendar
            dayStart.set(Calendar.HOUR_OF_DAY, 0)
            dayStart.set(Calendar.MINUTE, 0)
            dayStart.set(Calendar.SECOND, 0)
            dayStart.set(Calendar.MILLISECOND, 0)
            
            val dayEnd = dayStart.clone() as Calendar
            dayEnd.add(Calendar.DAY_OF_YEAR, 1)
            
            val dayEntries = getMoodEntriesByDateRange(dayStart.time, dayEnd.time)
            val averageIntensity = if (dayEntries.isEmpty()) 0.0 else dayEntries.map { it.intensity }.average()
            
            trend.add(Pair(dayStart.time, averageIntensity))
        }
        
        return trend.reversed()
    }

    private fun saveMoodEntries(entries: List<MoodEntry>) {
        val json = gson.toJson(entries)
        prefs.edit().putString("mood_entries", json).apply()
    }
}

