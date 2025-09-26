package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dailydose.R
import com.example.dailydose.adapters.HealthEntryAdapter
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.databinding.FragmentHistoryBinding
import com.example.dailydose.model.HealthEntry
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var healthRepository: HealthRepository
    private lateinit var healthEntryAdapter: HealthEntryAdapter
    private var currentFilter = FilterType.ALL

    enum class FilterType {
        ALL, TODAY, WEEK
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRepository()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }

    private fun setupRepository() {
        healthRepository = HealthRepository(requireContext())
    }

    private fun setupRecyclerView() {
        healthEntryAdapter = HealthEntryAdapter { entry ->
            // Handle entry click if needed
        }
        
        binding.rvHealthEntries.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = healthEntryAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnFilterAll.setOnClickListener {
            setFilter(FilterType.ALL)
        }
        
        binding.btnFilterToday.setOnClickListener {
            setFilter(FilterType.TODAY)
        }
        
        binding.btnFilterWeek.setOnClickListener {
            setFilter(FilterType.WEEK)
        }
    }

    private fun setFilter(filterType: FilterType) {
        currentFilter = filterType
        updateFilterButtons()
        loadData()
    }

    private fun updateFilterButtons() {
        // Reset all buttons
        binding.btnFilterAll.isSelected = false
        binding.btnFilterToday.isSelected = false
        binding.btnFilterWeek.isSelected = false
        
        // Select current filter
        when (currentFilter) {
            FilterType.ALL -> binding.btnFilterAll.isSelected = true
            FilterType.TODAY -> binding.btnFilterToday.isSelected = true
            FilterType.WEEK -> binding.btnFilterWeek.isSelected = true
        }
    }

    private fun loadData() {
        val entries = when (currentFilter) {
            FilterType.ALL -> healthRepository.getAllHealthEntries()
            FilterType.TODAY -> healthRepository.getTodayEntries()
            FilterType.WEEK -> healthRepository.getWeeklyEntries()
        }.sortedByDescending { it.date }
        
        healthEntryAdapter.updateEntries(entries)
        
        // Show/hide empty state
        if (entries.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvHealthEntries.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvHealthEntries.visibility = View.VISIBLE
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

