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
import java.util.*

    class DashboardFragment : Fragment() {

        private var _binding: FragmentDashboardBinding? = null
        private val binding get() = _binding!!

        private lateinit var healthRepository: HealthRepository
        private lateinit var healthViewModel: HealthViewModel
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
        // Quick action buttons
        binding.cardAddWeight?.setOnClickListener {
            navigateToAddEntry(HealthType.WEIGHT)
        }
        
        binding.cardAddBloodPressure?.setOnClickListener {
            navigateToAddEntry(HealthType.BLOOD_PRESSURE)
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
            // Navigate to AddEntryFragment with pre-selected type
            val bundle = Bundle().apply {
                putString("selected_type", healthType.name)
            }
            // Use findNavController for proper navigation
            findNavController().navigate(R.id.addEntryFragment, bundle)
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

        override fun onResume() {
            super.onResume()
            loadData() // Refresh data when returning to fragment
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
