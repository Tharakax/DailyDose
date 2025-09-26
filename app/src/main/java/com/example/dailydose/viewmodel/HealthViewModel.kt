package com.example.dailydose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthGoal
import com.example.dailydose.model.HealthType
import java.util.*

class HealthViewModel : ViewModel() {

    private lateinit var repository: HealthRepository

    private val _healthEntries = MutableLiveData<List<HealthEntry>>()
    val healthEntries: LiveData<List<HealthEntry>> = _healthEntries

    private val _healthGoals = MutableLiveData<List<HealthGoal>>()
    val healthGoals: LiveData<List<HealthGoal>> = _healthGoals

    private val _todayEntries = MutableLiveData<List<HealthEntry>>()
    val todayEntries: LiveData<List<HealthEntry>> = _todayEntries

    fun setRepository(repository: HealthRepository) {
        this.repository = repository
    }

    fun getAllHealthEntries() {
        _healthEntries.value = repository.getAllHealthEntries()
    }

    fun getHealthEntriesByType(type: HealthType) {
        _healthEntries.value = repository.getHealthEntriesByType(type)
    }

    fun getTodayEntries() {
        _todayEntries.value = repository.getTodayEntries()
    }

    fun getRecentEntries() {
        _healthEntries.value = repository.getRecentEntries()
    }

    fun getWeeklyEntries() {
        _healthEntries.value = repository.getWeeklyEntries()
    }

    fun getAllHealthGoals() {
        _healthGoals.value = repository.getAllHealthGoals()
    }

    fun getActiveGoals() {
        _healthGoals.value = repository.getActiveGoals()
    }

    fun saveHealthEntry(entry: HealthEntry) {
        repository.saveHealthEntry(entry)
        getAllHealthEntries()
        getTodayEntries()
    }

    fun updateHealthEntry(entry: HealthEntry) {
        repository.updateHealthEntry(entry)
        getAllHealthEntries()
        getTodayEntries()
    }

    fun deleteHealthEntry(entryId: String) {
        repository.deleteHealthEntry(entryId)
        getAllHealthEntries()
        getTodayEntries()
    }

    fun saveHealthGoal(goal: HealthGoal) {
        repository.saveHealthGoal(goal)
        getAllHealthGoals()
    }

    fun updateHealthGoal(goal: HealthGoal) {
        repository.updateHealthGoal(goal)
        getAllHealthGoals()
    }

    fun deleteHealthGoal(goalId: String) {
        repository.deleteHealthGoal(goalId)
        getAllHealthGoals()
    }
}
