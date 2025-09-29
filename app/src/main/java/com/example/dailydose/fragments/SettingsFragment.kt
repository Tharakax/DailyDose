package com.example.dailydose.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dailydose.R
import com.example.dailydose.databinding.FragmentSettingsBinding
import com.example.dailydose.utils.NotificationHelper

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationHelper: NotificationHelper

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
        
        try {
            initializeSettings()
            setupClickListeners()
            loadSettings()
            startAnimations()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error gracefully
        }
    }
    
    private fun initializeSettings() {
        sharedPreferences = requireContext().getSharedPreferences("health_app_prefs", 0)
        notificationHelper = NotificationHelper(requireContext())
    }
    
    private fun loadSettings() {
        // Load notification preferences
        val dailyReminders = sharedPreferences.getBoolean("daily_reminders", true)
        val waterReminders = sharedPreferences.getBoolean("water_reminders", true)
        val exerciseReminders = sharedPreferences.getBoolean("exercise_reminders", true)
        
        binding.switchDailyReminders?.isChecked = dailyReminders
        binding.switchWaterReminders?.isChecked = waterReminders
        binding.switchExerciseReminders?.isChecked = exerciseReminders
    }
    
    private fun startAnimations() {
        // Animate cards with staggered entrance
        binding.cardNotifications?.let { card ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
            card.startAnimation(animation)
        }
        
        binding.btnManageGoals?.let { button ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
            animation.startOffset = 200
            button.startAnimation(animation)
        }
        
        binding.btnAbout?.let { button ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
            animation.startOffset = 400
            button.startAnimation(animation)
        }
    }

    private fun setupClickListeners() {
        // Notification switches
        binding.switchDailyReminders?.setOnCheckedChangeListener { _, isChecked ->
            try {
                sharedPreferences.edit().putBoolean("daily_reminders", isChecked).apply()
                if (isChecked) {
                    notificationHelper.showDailyHealthReminder()
                    Toast.makeText(context, "Daily reminders enabled", Toast.LENGTH_SHORT).show()
                } else {
                    notificationHelper.cancelNotification(NotificationHelper.NOTIFICATION_ID_DAILY_REMINDER)
                    Toast.makeText(context, "Daily reminders disabled", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        binding.switchWaterReminders?.setOnCheckedChangeListener { _, isChecked ->
            try {
                sharedPreferences.edit().putBoolean("water_reminders", isChecked).apply()
                if (isChecked) {
                    notificationHelper.showWaterReminder()
                    Toast.makeText(context, "Water reminders enabled", Toast.LENGTH_SHORT).show()
                } else {
                    notificationHelper.cancelNotification(NotificationHelper.NOTIFICATION_ID_WATER_REMINDER)
                    Toast.makeText(context, "Water reminders disabled", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        binding.switchExerciseReminders?.setOnCheckedChangeListener { _, isChecked ->
            try {
                sharedPreferences.edit().putBoolean("exercise_reminders", isChecked).apply()
                if (isChecked) {
                    notificationHelper.showExerciseReminder()
                    Toast.makeText(context, "Exercise reminders enabled", Toast.LENGTH_SHORT).show()
                } else {
                    notificationHelper.cancelNotification(NotificationHelper.NOTIFICATION_ID_EXERCISE_REMINDER)
                    Toast.makeText(context, "Exercise reminders disabled", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Meditation button
        binding.btnMeditation?.setOnClickListener {
            try {
                // Navigate to meditation fragment
                val navController = findNavController()
                navController.navigate(R.id.meditationFragment)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Meditation feature coming soon", Toast.LENGTH_SHORT).show()
            }
        }

        // Manage Goals button
        binding.btnManageGoals?.setOnClickListener {
            Toast.makeText(context, "Goals management coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Export Data button
        binding.btnExportData?.setOnClickListener {
            Toast.makeText(context, "Export data feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Clear Data button
        binding.btnClearData?.setOnClickListener {
            Toast.makeText(context, "Clear data feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // About button
        binding.btnAbout?.setOnClickListener {
            Toast.makeText(context, "About Daily Health Tracker v1.0", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
