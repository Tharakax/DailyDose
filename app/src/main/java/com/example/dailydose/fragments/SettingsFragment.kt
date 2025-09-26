package com.example.dailydose.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailydose.R
import com.example.dailydose.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
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
