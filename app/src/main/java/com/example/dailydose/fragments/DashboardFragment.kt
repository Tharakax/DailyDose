package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dailydose.R
import com.example.dailydose.adapters.HealthEntryAdapter
import com.example.dailydose.adapters.QuickActionsAdapter
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.databinding.FragmentDashboardBinding
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthType
import com.example.dailydose.model.HabitEntry
import com.example.dailydose.model.HabitCategory
import com.example.dailydose.model.QuickAction
import com.example.dailydose.utils.BmiCalculator
import com.example.dailydose.viewmodel.HealthViewModel
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import com.example.dailydose.adapters.HealthTypeAdapter
import com.example.dailydose.databinding.DialogAddEntryBinding
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var healthRepository: HealthRepository
    private lateinit var healthViewModel: HealthViewModel
    private lateinit var todayEntriesAdapter: HealthEntryAdapter
    private lateinit var quickActionsAdapter: QuickActionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupViewModel()
            setupRecyclerViews()
            setupClickListeners()
            loadData()
            startAnimations()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error gracefully
        }
    }

    private fun setupViewModel() {
        healthRepository = HealthRepository(requireContext())
        healthViewModel = ViewModelProvider(this)[HealthViewModel::class.java]
        healthViewModel.setRepository(healthRepository)
    }

    private fun setupRecyclerViews() {
        // Quick Actions RecyclerView
        val quickActions = listOf(
            QuickAction("Add Habit", R.drawable.ic_add_habit, R.color.gradient_green),
            QuickAction("Meditation", R.drawable.ic_meditation, R.color.gradient_pink),
            QuickAction("BMI Calculator", R.drawable.ic_calculator, R.color.gradient_blue),
            QuickAction("History", R.drawable.ic_history, R.color.gradient_purple),
            QuickAction("Water", R.drawable.ic_water, R.color.gradient_teal),
            QuickAction("Exercise", R.drawable.ic_exercise, R.color.gradient_orange)
        )

        quickActionsAdapter = QuickActionsAdapter(quickActions) { action ->
            when (action.title) {
                "Add Habit" -> showAddHabitDialog()
                "Meditation" -> navigateToMeditation()
                "BMI Calculator" -> showBmiCalculatorDialog()
                "History" -> navigateToHistory()
                "Water" -> showAddWaterDialog()
                "Exercise" -> showAddExerciseDialog()
            }
        }

        binding.rvQuickActions?.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = quickActionsAdapter
        }

        // Today's entries RecyclerView
        todayEntriesAdapter = HealthEntryAdapter(
            onItemClick = { entry ->
                // Handle entry click if needed
            },
            onEditHabit = { entry ->
                showEditHabitDialog(entry)
            },
            onDeleteHabit = { entry ->
                showDeleteHabitDialog(entry)
            }
        )

        binding.rvTodayEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todayEntriesAdapter
        }
    }

    private fun setupClickListeners() {
        // BMI Calculate button
        binding.btnBmiCalculate?.setOnClickListener { view ->
            animateCardClick(view)
            showBmiCalculatorDialog()
        }
    }

    private fun navigateToMeditation() {
        try {
            findNavController().navigate(R.id.meditationFragment)
        } catch (e: Exception) {
            Toast.makeText(context, "Meditation feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHistory() {
        try {
            findNavController().navigate(R.id.historyFragment)
        } catch (e: Exception) {
            Toast.makeText(context, "History feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit_modern, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.ModernAlertDialog)
            .setView(dialogView)
            .create()

        // Setup dialog components
        setupHabitDialog(dialogView, dialog, null)
        dialog.show()
    }

    private fun showEditHabitDialog(entry: HealthEntry) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit_modern, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.ModernAlertDialog)
            .setView(dialogView)
            .create()

        // Extract habit info and setup dialog
        setupHabitDialog(dialogView, dialog, entry)
        dialog.show()
    }

    private fun setupHabitDialog(dialogView: View, dialog: AlertDialog, existingEntry: HealthEntry?) {
        val etTitle = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etHabitTitle)
        val etDescription = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etHabitDescription)
        val spinnerCategory = dialogView.findViewById<com.google.android.material.textfield.MaterialAutoCompleteTextView>(R.id.spinnerCategory)
        val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)

        // Setup category spinner
        val categories = HabitCategory.values().map { it.displayName }
        val adapter = android.widget.ArrayAdapter(requireContext(), R.layout.item_dropdown, categories)
        spinnerCategory.setAdapter(adapter)

        // Pre-fill if editing
        if (existingEntry != null) {
            val habitInfo = existingEntry.notes.substringAfter("HABIT: ").trim()
            val habitTitle = habitInfo.substringBefore(" - ").trim()
            val habitDescription = if (habitInfo.contains(" - ")) {
                habitInfo.substringAfter(" - ").substringBefore(" (").trim()
            } else ""
            val habitCategory = if (habitInfo.contains(" (")) {
                habitInfo.substringAfter(" (").substringBefore(")").trim()
            } else "General"

            etTitle.setText(habitTitle)
            etDescription.setText(habitDescription)
            spinnerCategory.setText(habitCategory, false)
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val selectedCategory = HabitCategory.values().find { it.displayName == spinnerCategory.text.toString() } ?: HabitCategory.GENERAL

            if (title.isEmpty()) {
                etTitle.error = "Please enter a habit title"
                return@setOnClickListener
            }

            val habit = HabitEntry(
                id = existingEntry?.id ?: UUID.randomUUID().toString(),
                title = title,
                description = description,
                category = selectedCategory.displayName,
                date = existingEntry?.date ?: Date(),
                timestamp = existingEntry?.timestamp ?: System.currentTimeMillis(),
                isCompleted = true,
                notes = if (description.isNotEmpty()) "$title - $description" else title
            )

            val healthEntry = HealthEntry(
                id = habit.id,
                type = HealthType.HABIT,
                value = 1.0,
                unit = "completed",
                date = habit.date,
                timestamp = habit.timestamp,
                notes = "HABIT: ${habit.title} - ${habit.description} (${habit.category})"
            )

            if (existingEntry != null) {
                healthRepository.updateHealthEntry(healthEntry)
                Toast.makeText(context, "⭐ Habit updated!", Toast.LENGTH_SHORT).show()
            } else {
                healthRepository.saveHealthEntry(healthEntry)
                Toast.makeText(context, "⭐ Habit added!", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
            loadData()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showDeleteHabitDialog(entry: HealthEntry) {
        AlertDialog.Builder(requireContext(), R.style.ModernAlertDialog)
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete this habit?")
            .setPositiveButton("Delete") { _, _ ->
                healthRepository.deleteHealthEntry(entry.id)
                Toast.makeText(context, "🗑️ Habit deleted!", Toast.LENGTH_SHORT).show()
                loadData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddWaterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_water_intake, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.ModernAlertDialog)
            .setView(dialogView)
            .create()

        val etWaterAmount = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etWaterAmount)
        val btnQuick250 = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnQuick250)
        val btnQuick500 = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnQuick500)
        val btnQuick1000 = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnQuick1000)
        val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)

        // Quick add buttons
        btnQuick250.setOnClickListener {
            etWaterAmount.setText("250")
        }
        btnQuick500.setOnClickListener {
            etWaterAmount.setText("500")
        }
        btnQuick1000.setOnClickListener {
            etWaterAmount.setText("1000")
        }

        btnSave.setOnClickListener {
            val amountText = etWaterAmount.text.toString().trim()
            if (amountText.isEmpty()) {
                etWaterAmount.error = "Please enter water amount"
                return@setOnClickListener
            }

            try {
                val amount = amountText.toDouble()
                if (amount <= 0) {
                    etWaterAmount.error = "Please enter a positive amount"
                    return@setOnClickListener
                }

                val waterEntry = HealthEntry(
                    id = UUID.randomUUID().toString(),
                    type = HealthType.WATER,
                    value = amount / 1000.0, // Convert ml to L
                    unit = "L",
                    date = Date(),
                    timestamp = System.currentTimeMillis(),
                    notes = "Water intake: ${amount}ml"
                )

                healthRepository.saveHealthEntry(waterEntry)
                Toast.makeText(context, "💧 Water intake added!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                loadData()
            } catch (e: NumberFormatException) {
                etWaterAmount.error = "Please enter a valid number"
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showAddExerciseDialog() {
        Toast.makeText(context, "Add Exercise feature coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        clearTestHabits()

        val allTodayEntries = healthRepository.getTodayEntries()
        val habitEntries = allTodayEntries.filter { entry ->
            entry.notes.startsWith("HABIT:") && !entry.notes.contains("Test Habit")
        }

        todayEntriesAdapter.updateEntries(habitEntries)
        updateHabitsCount(habitEntries.size)
        updateBMIDisplay()
        updateHealthStats()
    }

    private fun updateHabitsCount(count: Int) {
        binding.tvHabitsCount?.text = "$count/5"
    }

    private fun updateHealthStats() {
        // Simulate health data - in real app, fetch from repository
        binding.tvSteps?.text = "8,234"
        binding.tvWater?.text = "1.8L"
        binding.tvSleep?.text = "7.2h"
    }

    private fun clearTestHabits() {
        val allEntries = healthRepository.getAllHealthEntries()
        val realEntries = allEntries.filter { entry ->
            !entry.notes.contains("Test Habit") && !entry.id.startsWith("test-habit-")
        }

        if (allEntries.size != realEntries.size) {
            val sharedPrefs = requireContext().getSharedPreferences("health_data", android.content.Context.MODE_PRIVATE)
            val gson = com.google.gson.Gson()
            val json = gson.toJson(realEntries)
            sharedPrefs.edit().putString("health_entries", json).apply()
        }
    }

    private fun updateBMIDisplay() {
        val allEntries = healthRepository.getAllHealthEntries()
        val bmi = BmiCalculator.calculateBMIFromEntries(allEntries)

        if (bmi != null) {
            val category = BmiCalculator.getBMICategory(bmi)
            val advice = BmiCalculator.getBMIAdvice(bmi)
            val color = BmiCalculator.getBMIColor(bmi)

            binding.tvBmiValue?.text = String.format("%.1f", bmi)
            binding.tvBmiCategory?.text = category.displayName
            binding.tvBmiAdvice?.text = advice
            binding.tvBmiValue?.setTextColor(android.graphics.Color.parseColor(color))
            binding.cardBmi?.visibility = View.VISIBLE
        } else {
            binding.cardBmi?.visibility = View.GONE
        }
    }

    private fun showBmiCalculatorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_bmi_calculator_modern, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.ModernAlertDialog)
            .setView(dialogView)
            .create()

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCalculate)?.setOnClickListener {
            val weightText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etWeight)?.text.toString()
            val heightText = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etHeight)?.text.toString()

            if (weightText.isEmpty() || heightText.isEmpty()) {
                Toast.makeText(context, "Please enter both weight and height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val weight = weightText.toDouble()
                val height = heightText.toDouble() / 100.0
                val bmi = BmiCalculator.calculateBMI(weight, height)
                val category = BmiCalculator.getBMICategory(bmi)
                val advice = BmiCalculator.getBMIAdvice(bmi)

                val message = "Your BMI: ${String.format("%.1f", bmi)}\nCategory: ${category.displayName}\n\n$advice"

                AlertDialog.Builder(requireContext(), R.style.ModernAlertDialog)
                    .setTitle("BMI Result")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show()

                dialog.dismiss()
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startAnimations() {
        binding.welcomeSection?.let { welcomeSection ->
            val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_gentle)
            welcomeSection.startAnimation(fadeIn)
        }

        binding.rvQuickActions?.let { rv ->
            val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up_gentle)
            slideUp.startOffset = 150
            rv.startAnimation(slideUp)
        }

        binding.rvTodayEntries?.let { rv ->
            val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up_gentle)
            slideUp.startOffset = 300
            rv.startAnimation(slideUp)
        }

        binding.cardBmi?.let { card ->
            val scaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in_gentle)
            scaleIn.startOffset = 450
            card.startAnimation(scaleIn)
        }
    }

    private fun animateCardClick(view: View) {
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_in)
        scaleDown.duration = 100

        view.startAnimation(scaleDown)

        scaleDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                val scaleUp = AnimationUtils.loadAnimation(context, R.anim.bounce_in)
                scaleUp.duration = 200
                view.startAnimation(scaleUp)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}