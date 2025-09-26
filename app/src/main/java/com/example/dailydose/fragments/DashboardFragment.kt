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

        setupViewModel()
        setupRecyclerViews()
        setupClickListeners()
        loadData()
        startAnimations()
    }

        private fun setupViewModel() {
            healthRepository = HealthRepository(requireContext())
            healthViewModel = ViewModelProvider(this)[HealthViewModel::class.java]
            healthViewModel.setRepository(healthRepository)
        }

    private fun setupRecyclerViews() {
        // Today's entries RecyclerView
        todayEntriesAdapter = HealthEntryAdapter { entry ->
            // Handle entry click if needed
        }
        binding.rvTodayEntries?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = todayEntriesAdapter
        }

        // Goals RecyclerView
        goalsAdapter = GoalAdapter { goal ->
            // Handle goal click if needed
        }
        binding.rvGoals?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalsAdapter
        }
    }

    private fun setupClickListeners() {
        // Add Entry button with animation
        binding.cardAddEntry?.setOnClickListener { view ->
            animateCardClick(view)
            showAddEntryDialog()
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
                    navigateToAddEntry(HealthType.HEART_RATE)
                }
            } catch (e: Exception) {
                // Card might not exist in portrait layout
            }

            try {
                val stepsCard = binding.root.findViewById<View>(R.id.card_add_steps)
                stepsCard?.setOnClickListener {
                    navigateToAddEntry(HealthType.STEPS)
                }
            } catch (e: Exception) {
                // Card might not exist in portrait layout
            }
        }

        private fun navigateToAddEntry(healthType: HealthType) {
            showAddEntryDialog(healthType)
        }

        private fun showAddEntryDialog(preSelectedType: HealthType? = null) {
            val dialogBinding = DialogAddEntryBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

            var selectedHealthType: HealthType? = preSelectedType

            // Setup health type dropdown
            val healthTypes = HealthType.values().toList()
            val healthTypeNames = healthTypes.map { it.displayName }
            
            // Set initial selection if pre-selected
            preSelectedType?.let { 
                dialogBinding.etHealthType.setText(it.displayName)
                updateValueHint(dialogBinding, it)
            }

            // Health type dropdown click listener
            dialogBinding.etHealthType.setOnClickListener {
                showHealthTypeSelectionDialog(healthTypes) { selectedType ->
                    selectedHealthType = selectedType
                    dialogBinding.etHealthType.setText(selectedType.displayName)
                    updateValueHint(dialogBinding, selectedType)
                }
            }

            // Save button
            dialogBinding.btnSave.setOnClickListener {
                val title = dialogBinding.etTitle.text.toString().trim()
                val valueText = dialogBinding.etValue.text.toString().trim()
                val description = dialogBinding.etDescription.text.toString().trim()

                if (title.isEmpty()) {
                    Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (selectedHealthType == null) {
                    Toast.makeText(context, "Please select a health type", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (valueText.isEmpty()) {
                    Toast.makeText(context, "Please enter a value", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val value = try {
                    valueText.toDouble()
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val entry = HealthEntry(
                    id = UUID.randomUUID().toString(),
                    type = selectedHealthType!!,
                    value = value,
                    unit = selectedHealthType!!.unit,
                    date = Date(),
                    timestamp = System.currentTimeMillis(),
                    notes = if (description.isNotEmpty()) "$title - $description" else title
                )

                healthRepository.saveHealthEntry(entry)
                Toast.makeText(context, "Entry saved successfully", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
                loadData() // Refresh dashboard data
            }

            // Cancel button
            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun updateValueHint(binding: DialogAddEntryBinding, type: HealthType) {
            binding.tilValue.hint = "Enter ${type.displayName} (${type.unit})"
        }

        private fun showHealthTypeSelectionDialog(healthTypes: List<HealthType>, onTypeSelected: (HealthType) -> Unit) {
            val typeNames = healthTypes.map { it.displayName }.toTypedArray()
            
            AlertDialog.Builder(requireContext())
                .setTitle("Select Health Type")
                .setItems(typeNames) { _, which ->
                    val selectedType = healthTypes[which]
                    onTypeSelected(selectedType)
                }
                .show()
        }

    private fun loadData() {
        // Load today's entries
        val todayEntries = healthRepository.getTodayEntries()
        todayEntriesAdapter.updateEntries(todayEntries)

        // Load active goals
        val activeGoals = healthRepository.getActiveGoals()
        goalsAdapter.updateGoals(activeGoals)
        
        // Calculate and display BMI
        updateBMIDisplay()
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
        // Animate welcome section
        binding.root.findViewById<View>(R.id.welcome_section)?.let { welcomeSection ->
            val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            welcomeSection.startAnimation(fadeIn)
        }

        // Animate quick action cards with staggered delay
        binding.cardAddEntry?.let { card ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom)
            slideIn.startOffset = 100
            card.startAnimation(slideIn)
        }

        binding.cardBmiCalculator?.let { card ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom)
            slideIn.startOffset = 200
            card.startAnimation(slideIn)
        }

        // Animate BMI card
        binding.cardBmi?.let { card ->
            val scaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in)
            scaleIn.startOffset = 300
            card.startAnimation(scaleIn)
        }

        // Animate RecyclerViews
        binding.rvTodayEntries?.let { rv ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
            slideIn.startOffset = 400
            rv.startAnimation(slideIn)
        }

        binding.rvGoals?.let { rv ->
            val slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right)
            slideIn.startOffset = 500
            rv.startAnimation(slideIn)
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
