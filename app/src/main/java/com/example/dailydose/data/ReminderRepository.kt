package com.example.dailydose.data

import android.content.Context
import android.content.SharedPreferences
import com.example.dailydose.model.Reminder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ReminderRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("reminder_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveReminder(reminder: Reminder) {
        val reminders = getAllReminders().toMutableList()
        reminders.add(reminder)
        saveReminders(reminders)
    }

    fun getAllReminders(): List<Reminder> {
        val json = sharedPreferences.getString("reminders", "[]")
        val type = object : TypeToken<List<Reminder>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun getActiveReminders(): List<Reminder> {
        return getAllReminders().filter { it.isActive }
    }

    fun getUpcomingReminders(): List<Reminder> {
        val now = Date()
        return getActiveReminders().filter { it.reminderTime > now }
            .sortedBy { it.reminderTime }
    }

    fun updateReminder(reminder: Reminder) {
        val reminders = getAllReminders().toMutableList()
        val index = reminders.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            reminders[index] = reminder
            saveReminders(reminders)
        }
    }

    fun deleteReminder(reminderId: String) {
        val reminders = getAllReminders().toMutableList()
        reminders.removeAll { it.id == reminderId }
        saveReminders(reminders)
    }

    fun toggleReminderActive(reminderId: String) {
        val reminders = getAllReminders().toMutableList()
        val index = reminders.indexOfFirst { it.id == reminderId }
        if (index != -1) {
            reminders[index] = reminders[index].copy(isActive = !reminders[index].isActive)
            saveReminders(reminders)
        }
    }

    private fun saveReminders(reminders: List<Reminder>) {
        val json = gson.toJson(reminders)
        sharedPreferences.edit().putString("reminders", json).apply()
    }
}

