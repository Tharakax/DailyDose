package com.example.dailydose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthGoal

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
        loadAllData()
    }
    
    fun loadAllData() {
        _healthEntries.value = repository.getAllHealthEntries()
        _healthGoals.value = repository.getAllHealthGoals()
        _todayEntries.value = repository.getTodayEntries()
    }
    
    fun saveHealthEntry(entry: HealthEntry) {
        repository.saveHealthEntry(entry)
        loadAllData()
    }
    
    fun deleteHealthEntry(entryId: String) {
        repository.deleteHealthEntry(entryId)
        loadAllData()
    }
    
    fun saveHealthGoal(goal: HealthGoal) {
        repository.saveHealthGoal(goal)
        loadAllData()
    }
    
    fun deleteHealthGoal(goalId: String) {
        repository.deleteHealthGoal(goalId)
        loadAllData()
    }
    
    fun updateGoalProgress(goalId: String, currentValue: Double) {
        repository.updateGoalProgress(goalId, currentValue)
        loadAllData()
    }
}
