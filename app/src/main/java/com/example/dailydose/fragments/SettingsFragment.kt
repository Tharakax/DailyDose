package com.example.dailydose.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailydose.R
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var healthRepository: HealthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRepository()
        setupClickListeners()
    }

    private fun setupRepository() {
        healthRepository = HealthRepository(requireContext())
    }

    private fun setupClickListeners() {
        binding.btnManageGoals.setOnClickListener {
            // Navigate to goals management (could be a dialog or new fragment)
            showGoalsDialog()
        }
        
        binding.btnExportData.setOnClickListener {
            exportData()
        }
        
        binding.btnClearData.setOnClickListener {
            showClearDataDialog()
        }
        
        binding.btnAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun showGoalsDialog() {
        // Simple goals management dialog
        val goals = healthRepository.getActiveGoals()
        val goalTexts = if (goals.isEmpty()) {
            arrayOf("No active goals")
        } else {
            goals.map { "${it.type.displayName}: ${it.currentValue}/${it.targetValue} ${it.type.unit}" }.toTypedArray()
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Your Health Goals")
            .setItems(goalTexts) { _, _ -> }
            .setPositiveButton("Add Goal") { _, _ ->
                // Navigate to add goal (simplified for now)
                Toast.makeText(context, "Goal management coming soon!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun exportData() {
        val entries = healthRepository.getAllHealthEntries()
        val goals = healthRepository.getAllHealthGoals()
        
        val exportText = buildString {
            appendLine("Health Data Export")
            appendLine("==================")
            appendLine()
            appendLine("Health Entries (${entries.size}):")
            entries.forEach { entry ->
                appendLine("${entry.type.displayName}: ${entry.value} ${entry.unit} - ${entry.date}")
                if (entry.notes.isNotEmpty()) {
                    appendLine("  Notes: ${entry.notes}")
                }
                appendLine()
            }
            appendLine("Health Goals (${goals.size}):")
            goals.forEach { goal ->
                appendLine("${goal.type.displayName}: ${goal.currentValue}/${goal.targetValue} ${goal.type.unit}")
            }
        }
        
        // In a real app, you would save this to a file or share it
        Toast.makeText(context, "Data exported (${entries.size} entries, ${goals.size} goals)", Toast.LENGTH_LONG).show()
    }

    private fun showClearDataDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("Are you sure you want to clear all health data? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun clearAllData() {
        // Clear SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("health_data", android.content.Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        
        Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("About Daily Health Tracker")
            .setMessage("Version 1.0.0\n\nA simple health tracking app to monitor your daily health metrics.\n\nFeatures:\n• Track various health metrics\n• Set and monitor goals\n• View history and trends\n• Export your data")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
