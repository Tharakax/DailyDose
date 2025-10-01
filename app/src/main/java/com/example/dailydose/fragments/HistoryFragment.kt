package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailydose.R
import com.example.dailydose.adapters.HistoryHabitAdapter
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.model.HealthEntry
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var healthRepository: HealthRepository
    private lateinit var historyAdapter: HistoryHabitAdapter
    private var allHabitEntries: List<HealthEntry> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupRepository()
            setupRecyclerView()
            setupClickListeners()
            loadHistoryData()
            updateStats()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRepository() {
        healthRepository = HealthRepository(requireContext())
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryHabitAdapter(
            habits = emptyList(),
            onHabitClick = { habit ->
                // Handle habit click if needed
                android.widget.Toast.makeText(context, "Habit: ${habit.notes}", android.widget.Toast.LENGTH_SHORT).show()
            }
        )

        val recyclerView = requireView().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_history_habits)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupClickListeners() {
        val view = requireView()
        
        // Back button
        view.findViewById<ImageButton>(R.id.btn_back_history).setOnClickListener {
            findNavController().navigateUp()
        }

        // Clear all button
        view.findViewById<ImageButton>(R.id.btn_clear_all_history).setOnClickListener {
            showClearAllDialog()
        }

        // Export button
        view.findViewById<Button>(R.id.btn_export_history).setOnClickListener {
            android.widget.Toast.makeText(context, "📤 Export feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
        }

        // Add first habit button
        view.findViewById<Button>(R.id.btn_add_first_habit).setOnClickListener {
            findNavController().navigate(R.id.dashboardFragment)
        }

        // Filter spinner
        view.findViewById<android.widget.Spinner>(R.id.spinner_filter).onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filterOptions = arrayOf("All", "Today", "This Week", "This Month")
                val selectedFilter = filterOptions[position]
                historyAdapter.filterHabits(selectedFilter)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun loadHistoryData() {
        val view = requireView()
        
        // Get all habit entries
        val allEntries = healthRepository.getAllHealthEntries()
        allHabitEntries = allEntries.filter { entry ->
            entry.notes.startsWith("HABIT:") && !entry.notes.contains("Test Habit")
        }

        // Setup filter spinner
        val filterOptions = arrayOf("All", "Today", "This Week", "This Month")
        val filterAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.findViewById<android.widget.Spinner>(R.id.spinner_filter).adapter = filterAdapter

        // Update adapter
        historyAdapter.updateHabits(allHabitEntries)

        // Show/hide empty state
        if (allHabitEntries.isEmpty()) {
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_history_habits).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.layout_empty_history).visibility = View.VISIBLE
        } else {
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_history_habits).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.layout_empty_history).visibility = View.GONE
        }
    }

    private fun updateStats() {
        val view = requireView()
        
        // Calculate total habits
        val totalHabits = allHabitEntries.size
        view.findViewById<TextView>(R.id.tv_total_habits).text = totalHabits.toString()

        // Calculate this week's habits
        val thisWeekHabits = allHabitEntries.filter { entry ->
            isThisWeek(entry.date)
        }.size
        view.findViewById<TextView>(R.id.tv_week_habits).text = thisWeekHabits.toString()
    }

    private fun isThisWeek(date: Date): Boolean {
        val today = Calendar.getInstance()
        val habitDate = Calendar.getInstance().apply { time = date }
        val weekAgo = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -1)
        }
        return habitDate.after(weekAgo.time) && habitDate.before(today.time)
    }

    private fun showClearAllDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Clear History")
            .setMessage("Are you sure you want to clear all habit history? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                clearAllHabitHistory()
                android.widget.Toast.makeText(context, "🗑️ All habit history cleared!", android.widget.Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearAllHabitHistory() {
        // Get all entries and remove habit entries
        val allEntries = healthRepository.getAllHealthEntries()
        val nonHabitEntries = allEntries.filter { entry ->
            !entry.notes.startsWith("HABIT:")
        }

        // Save the filtered entries back
        val sharedPrefs = requireContext().getSharedPreferences("health_data", android.content.Context.MODE_PRIVATE)
        val gson = com.google.gson.Gson()
        val json = gson.toJson(nonHabitEntries)
        sharedPrefs.edit().putString("health_entries", json).apply()

        // Refresh data
        loadHistoryData()
        updateStats()
    }

    override fun onResume() {
        super.onResume()
        loadHistoryData()
        updateStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}