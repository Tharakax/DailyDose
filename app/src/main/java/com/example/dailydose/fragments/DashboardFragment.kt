    package com.example.dailydose.fragments

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import androidx.navigation.fragment.findNavController
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.dailydose.R
    import com.example.dailydose.adapters.GoalAdapter
    import com.example.dailydose.adapters.HealthEntryAdapter
    import com.example.dailydose.data.HealthRepository
    import com.example.dailydose.databinding.FragmentDashboardBinding
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthType
import com.example.dailydose.model.HabitEntry
import com.example.dailydose.model.HabitCategory
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
        private lateinit var healthViewModel:HealthViewModel
        private lateinit var todayEntriesAdapter: HealthEntryAdapter
        private lateinit var goalsAdapter: GoalAdapter

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
        binding.rvTodayEntries?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todayEntriesAdapter
            visibility = View.VISIBLE
            setHasFixedSize(false)
        }
        
        // Note: Test habit creation removed - now showing real habits only

        // Goals RecyclerView
        goalsAdapter = GoalAdapter { goal ->
            // Handle goal click if needed
        }
        binding.rvGoals?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalsAdapter
        }
    }

    private fun setupClickListeners() {
        // Add Habit button with animation
        binding.cardAddEntry?.setOnClickListener { view ->
            animateCardClick(view)
            showAddHabitDialog()
        }

        // Meditation button click listener
        binding.cardMeditation?.setOnClickListener { view ->
            animateCardClick(view)
            navigateToMeditation()
        }

        // BMI Calculator button click listener
        binding.cardBmiCalculator?.setOnClickListener { view ->
            animateCardClick(view)
            showBmiCalculatorDialog()
        }

        // BMI Card click listener (existing BMI display)
        binding.cardBmi?.setOnClickListener { view ->
            animateCardClick(view)
            showBmiCalculatorDialog()
        }


            // Additional cards for landscape layout (only if they exist)
            try {
                val heartRateCard = binding.root.findViewById<View>(R.id.card_add_heart_rate)
                heartRateCard?.setOnClickListener {
                    showAddHabitDialog()
                }
            } catch (e: Exception) {
                // Card might not exist in portrait layout
            }

            try {
                val stepsCard = binding.root.findViewById<View>(R.id.card_add_steps)
                stepsCard?.setOnClickListener {
                    showAddHabitDialog()
                }
            } catch (e: Exception) {
                // Card might not exist in portrait layout
            }
        }


        private fun navigateToMeditation() {
            try {
                val navController = findNavController()
                navController.navigate(R.id.meditationFragment)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Meditation feature coming soon", Toast.LENGTH_SHORT).show()
            }
        }

        private fun showAddHabitDialog() {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Add New Habit")
                .create()

            val etTitle = dialogView.findViewById<android.widget.EditText>(R.id.etHabitTitle)
            val etDescription = dialogView.findViewById<android.widget.EditText>(R.id.etHabitDescription)
            val spinnerCategory = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerCategory)
            val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)
            val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)

            // Setup category spinner
            val categories = HabitCategory.values().map { it.displayName }
            val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter

            // Save button
            btnSave.setOnClickListener {
                val title = etTitle.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val selectedCategory = HabitCategory.values()[spinnerCategory.selectedItemPosition]

                if (title.isEmpty()) {
                    Toast.makeText(context, "Please enter a habit title", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val habit = HabitEntry(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    category = selectedCategory.displayName,
                    date = Date(),
                    timestamp = System.currentTimeMillis(),
                    isCompleted = true,
                    notes = if (description.isNotEmpty()) "$title - $description" else title
                )

                // Save as HealthEntry for compatibility with existing system
                val healthEntry = HealthEntry(
                    id = habit.id,
                    type = HealthType.WATER, // Use WATER type as placeholder for habits
                    value = 1.0, // Each habit counts as 1
                    unit = "habit",
                    date = habit.date,
                    timestamp = habit.timestamp,
                    notes = "HABIT: ${habit.title} - ${habit.description} (${habit.category})"
                )

                healthRepository.saveHealthEntry(healthEntry)
                Toast.makeText(context, "⭐ Habit added successfully!", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
                loadData() // Refresh dashboard data
            }

            // Cancel button
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    private fun showEditHabitDialog(entry: HealthEntry) {
        // Extract habit information from notes
        val habitInfo = entry.notes.substringAfter("HABIT: ").trim()
        val habitTitle = habitInfo.substringBefore(" - ").trim()
        val habitDescription = if (habitInfo.contains(" - ")) {
            habitInfo.substringAfter(" - ").substringBefore(" (").trim()
        } else {
            ""
        }
        val habitCategory = if (habitInfo.contains(" (")) {
            habitInfo.substringAfter(" (").substringBefore(")").trim()
        } else {
            "General"
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Habit")
            .create()

        val etTitle = dialogView.findViewById<android.widget.EditText>(R.id.etHabitTitle)
        val etDescription = dialogView.findViewById<android.widget.EditText>(R.id.etHabitDescription)
        val spinnerCategory = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerCategory)
        val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)

        // Pre-fill with existing data
        etTitle.setText(habitTitle)
        etDescription.setText(habitDescription)

        // Setup category spinner
        val categories = HabitCategory.values().map { it.displayName }
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Set selected category
        val categoryIndex = categories.indexOf(habitCategory)
        if (categoryIndex >= 0) {
            spinnerCategory.setSelection(categoryIndex)
        }

        // Save button
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val selectedCategory = HabitCategory.values()[spinnerCategory.selectedItemPosition]

            if (title.isEmpty()) {
                Toast.makeText(context, "Please enter a habit title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update the existing entry
            val updatedEntry = entry.copy(
                notes = "HABIT: $title - $description (${selectedCategory.displayName})"
            )

            healthRepository.updateHealthEntry(updatedEntry)
            Toast.makeText(context, "⭐ Habit updated successfully!", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
            loadData() // Refresh dashboard data
        }

        // Cancel button
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteHabitDialog(entry: HealthEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete this habit? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                healthRepository.deleteHealthEntry(entry.id)
                Toast.makeText(context, "🗑️ Habit deleted successfully!", Toast.LENGTH_SHORT).show()
                loadData() // Refresh dashboard data
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadData() {
        // Clear any test habits first
        clearTestHabits()
        
        // Load today's entries and filter for habits only
        val allTodayEntries = healthRepository.getTodayEntries()
        val habitEntries = allTodayEntries.filter { entry ->
            entry.notes.startsWith("HABIT:") && !entry.notes.contains("Test Habit")
        }
        
        // Load habit entries for display
        
        // Display only real habit entries in Today's Summary
        todayEntriesAdapter.updateEntries(habitEntries)

        // Load active goals
        val activeGoals = healthRepository.getActiveGoals()
        goalsAdapter.updateGoals(activeGoals)
        
        // Calculate and display BMI
        updateBMIDisplay()
    }
    
    private fun clearTestHabits() {
        // Get all entries and remove any test habits
        val allEntries = healthRepository.getAllHealthEntries()
        val realEntries = allEntries.filter { entry ->
            !entry.notes.contains("Test Habit") && !entry.id.startsWith("test-habit-")
        }
        
        // If we found test habits, clear them
        if (allEntries.size != realEntries.size) {
            println("Dashboard: Clearing ${allEntries.size - realEntries.size} test habits")
            
            // Clear all entries and save only real ones
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
            
            // Set color based on BMI category
            binding.tvBmiValue?.setTextColor(android.graphics.Color.parseColor(color))
            binding.cardBmi?.visibility = View.VISIBLE
        } else {
            binding.cardBmi?.visibility = View.GONE
        }
    }

    private fun showBmiCalculatorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_bmi_calculator, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("BMI Calculator")
            .create()
        
        // Calculate button
        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_calculate)?.setOnClickListener {
            val weightText = dialogView.findViewById<android.widget.EditText>(R.id.et_weight).text.toString()
            val heightText = dialogView.findViewById<android.widget.EditText>(R.id.et_height).text.toString()
            
            if (weightText.isEmpty() || heightText.isEmpty()) {
                Toast.makeText(context, "Please enter both weight and height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            try {
                val weight = weightText.toDouble()
                val height = heightText.toDouble() / 100.0 // Convert cm to meters
                val bmi = BmiCalculator.calculateBMI(weight, height)
                val category = BmiCalculator.getBMICategory(bmi)
                val advice = BmiCalculator.getBMIAdvice(bmi)
                
                val message = "Your BMI: ${String.format("%.1f", bmi)}\nCategory: ${category.displayName}\nAdvice: $advice"
                
                AlertDialog.Builder(requireContext())
                    .setTitle("BMI Result")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show()
                    
                dialog.dismiss()
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Cancel button
        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_cancel)?.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }


    private fun startAnimations() {
        // Animate welcome section with gentle fade
        binding.root.findViewById<View>(R.id.welcome_section)?.let { welcomeSection ->
            val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_gentle)
            welcomeSection.startAnimation(fadeIn)
        }

        // Animate quick action cards with gentle slide up
        binding.cardAddEntry?.let { card ->
            val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up_gentle)
            slideUp.startOffset = 150
            card.startAnimation(slideUp)
        }

        binding.cardBmiCalculator?.let { card ->
            val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up_gentle)
            slideUp.startOffset = 300
            card.startAnimation(slideUp)
        }

        // Animate BMI card with gentle scale
        binding.cardBmi?.let { card ->
            val scaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in_gentle)
            scaleIn.startOffset = 450
            card.startAnimation(scaleIn)
        }

        // Animate RecyclerViews with gentle slide up
        binding.rvTodayEntries?.let { rv ->
            val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up_gentle)
            slideUp.startOffset = 600
            rv.startAnimation(slideUp)
        }

        binding.rvGoals?.let { rv ->
            val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up_gentle)
            slideUp.startOffset = 750
            rv.startAnimation(slideUp)
        }
    }

    private fun animateCardClick(view: View) {
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_in)
        scaleDown.duration = 100
        scaleDown.interpolator = android.view.animation.DecelerateInterpolator()
        
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
            loadData() // Refresh data when returning to fragment
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
