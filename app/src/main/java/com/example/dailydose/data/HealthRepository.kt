package com.example.dailydose.data

import android.content.Context
import android.content.SharedPreferences
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthGoal
import com.example.dailydose.model.HealthType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class HealthRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("health_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Health Entries
    fun saveHealthEntry(entry: HealthEntry) {
        val entries = getAllHealthEntries().toMutableList()
        entries.add(entry)
        saveEntries(entries)
    }

    fun getAllHealthEntries(): List<HealthEntry> {
        val json = sharedPreferences.getString("health_entries", "[]")
        val type = object : TypeToken<List<HealthEntry>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun getHealthEntriesByType(type: HealthType): List<HealthEntry> {
        return getAllHealthEntries().filter { it.type == type }
    }

    fun getRecentEntries(limit: Int = 10): List<HealthEntry> {
        return getAllHealthEntries()
            .sortedByDescending { it.date }
            .take(limit)
    }

    fun updateHealthEntry(entry: HealthEntry) {
        val entries = getAllHealthEntries().toMutableList()
        val entryIndex = entries.indexOfFirst { it.id == entry.id }
        if (entryIndex != -1) {
            entries[entryIndex] = entry
            saveEntries(entries)
        }
    }

    fun deleteHealthEntry(entryId: String) {
        val entries = getAllHealthEntries().toMutableList()
        entries.removeAll { it.id == entryId }
        saveEntries(entries)
    }

    private fun saveEntries(entries: List<HealthEntry>) {
        val json = gson.toJson(entries)
        sharedPreferences.edit().putString("health_entries", json).apply()
    }

    // Health Goals
    fun saveHealthGoal(goal: HealthGoal) {
        val goals = getAllHealthGoals().toMutableList()
        goals.add(goal)
        saveGoals(goals)
    }

    fun getAllHealthGoals(): List<HealthGoal> {
        val json = sharedPreferences.getString("health_goals", "[]")
        val type = object : TypeToken<List<HealthGoal>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun getActiveGoals(): List<HealthGoal> {
        return getAllHealthGoals().filter { it.isActive }
    }

    fun updateHealthGoal(goal: HealthGoal) {
        val goals = getAllHealthGoals().toMutableList()
        val goalIndex = goals.indexOfFirst { it.id == goal.id }
        if (goalIndex != -1) {
            goals[goalIndex] = goal
            saveGoals(goals)
        }
    }

    fun updateGoalProgress(goalId: String, currentValue: Double) {
        val goals = getAllHealthGoals().toMutableList()
        val goalIndex = goals.indexOfFirst { it.id == goalId }
        if (goalIndex != -1) {
            goals[goalIndex] = goals[goalIndex].copy(currentValue = currentValue)
            saveGoals(goals)
        }
    }

    fun deleteHealthGoal(goalId: String) {
        val goals = getAllHealthGoals().toMutableList()
        goals.removeAll { it.id == goalId }
        saveGoals(goals)
    }

    private fun saveGoals(goals: List<HealthGoal>) {
        val json = gson.toJson(goals)
        sharedPreferences.edit().putString("health_goals", json).apply()
    }

    // Statistics
    fun getTodayEntries(): List<HealthEntry> {
        val calendar = java.util.Calendar.getInstance()
        val today = calendar.time
        
        // Set to start of today
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        
        // Set to end of today
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.time
        
        return getAllHealthEntries().filter { entry ->
            entry.date >= startOfDay && entry.date < endOfDay
        }
    }

    fun getWeeklyEntries(): List<HealthEntry> {
        val weekAgo = Date(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000))
        return getAllHealthEntries().filter { it.date >= weekAgo }
    }
}

