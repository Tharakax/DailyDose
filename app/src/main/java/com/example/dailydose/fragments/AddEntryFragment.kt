package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailydose.R
import com.example.dailydose.adapters.HealthTypeAdapter
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.databinding.FragmentAddEntryBinding
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthType
import java.util.*

class AddEntryFragment : Fragment() {

    private var _binding: FragmentAddEntryBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var healthRepository: HealthRepository
    private lateinit var healthTypeAdapter: HealthTypeAdapter
    private var selectedHealthType: HealthType? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRepository()
        setupRecyclerView()
        setupClickListeners()
        handleArguments()
    }

    private fun setupRepository() {
        healthRepository = HealthRepository(requireContext())
    }

    private fun setupRecyclerView() {
        healthTypeAdapter = HealthTypeAdapter(
            healthTypes = HealthType.values().toList(),
            selectedType = selectedHealthType,
            onTypeSelected = { type ->
                selectedHealthType = type
                updateValueHint()
            }
        )
        
        binding.rvHealthTypes.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = healthTypeAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveEntry()
        }
    }

    private fun handleArguments() {
        arguments?.getString("selected_type")?.let { typeName ->
            val type = HealthType.valueOf(typeName)
            selectedHealthType = type
            healthTypeAdapter.updateSelectedType(type)
            updateValueHint()
        }
    }

    private fun updateValueHint() {
        selectedHealthType?.let { type ->
            binding.tilValue.hint = "Enter ${type.displayName} (${type.unit})"
        }
    }

    private fun saveEntry() {
        val valueText = binding.etValue.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        
        if (selectedHealthType == null) {
            Toast.makeText(context, "Please select a health type", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (valueText.isEmpty()) {
            Toast.makeText(context, "Please enter a value", Toast.LENGTH_SHORT).show()
            return
        }
        
        val value = try {
            valueText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }
        
        val entry = HealthEntry(
            id = UUID.randomUUID().toString(),
            type = selectedHealthType!!,
            value = value,
            unit = selectedHealthType!!.unit,
            date = Date(),
            notes = notes
        )
        
        healthRepository.saveHealthEntry(entry)
        
        Toast.makeText(context, "Entry saved successfully", Toast.LENGTH_SHORT).show()
        
        // Clear form
        binding.etValue.text?.clear()
        binding.etNotes.text?.clear()
        selectedHealthType = null
        healthTypeAdapter.updateSelectedType(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
