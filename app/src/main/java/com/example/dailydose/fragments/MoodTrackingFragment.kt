package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailydose.R
import com.example.dailydose.adapters.MoodEntryAdapter
import com.example.dailydose.data.MoodRepository
import com.example.dailydose.databinding.FragmentMoodTrackingBinding
import com.example.dailydose.model.MoodEntry
import com.example.dailydose.model.MoodType
import java.text.SimpleDateFormat
import java.util.*

class MoodTrackingFragment : Fragment() {

    private var _binding: FragmentMoodTrackingBinding? = null
    private val binding get() = _binding!!

    private lateinit var moodRepository: MoodRepository
    private lateinit var moodEntryAdapter: MoodEntryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRepository()
        setupRecyclerView()
        setupMoodButtons()
        loadData()
    }

    private fun setupRepository() {
        moodRepository = MoodRepository(requireContext())
    }

    private fun setupRecyclerView() {
        moodEntryAdapter = MoodEntryAdapter { moodEntry ->
            // Handle mood entry click if needed
            Toast.makeText(context, "Mood: ${moodEntry.mood.displayName}", Toast.LENGTH_SHORT).show()
        }

        binding.rvMoodEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodEntryAdapter
        }
    }

    private fun setupMoodButtons() {
        // Positive moods
        binding.btnVeryHappy.setOnClickListener { logMood(MoodType.VERY_HAPPY) }
        binding.btnHappy.setOnClickListener { logMood(MoodType.HAPPY) }
        binding.btnExcited.setOnClickListener { logMood(MoodType.EXCITED) }

        // Neutral moods
        binding.btnNeutral.setOnClickListener { logMood(MoodType.NEUTRAL) }
        binding.btnCalm.setOnClickListener { logMood(MoodType.CALM) }
        binding.btnTired.setOnClickListener { logMood(MoodType.TIRED) }

        // Negative moods
        binding.btnSad.setOnClickListener { logMood(MoodType.SAD) }
        binding.btnAngry.setOnClickListener { logMood(MoodType.ANGRY) }
        binding.btnAnxious.setOnClickListener { logMood(MoodType.ANXIOUS) }
        binding.btnVerySad.setOnClickListener { logMood(MoodType.VERY_SAD) }
    }

    private fun logMood(moodType: MoodType) {
        val moodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            mood = moodType,
            intensity = 5, // Default intensity
            notes = "",
            date = Date(),
            timestamp = System.currentTimeMillis()
        )

        moodRepository.saveMoodEntry(moodEntry)
        Toast.makeText(context, "Mood logged: ${moodType.emoji} ${moodType.displayName}", Toast.LENGTH_SHORT).show()
        
        loadData()
    }

    private fun loadData() {
        val todayEntries = moodRepository.getTodayMoodEntries()
        val allEntries = moodRepository.getAllMoodEntries()
        val statistics = moodRepository.getMoodStatistics()
        val averageIntensity = moodRepository.getAverageMoodIntensity()

        // Update current mood display
        updateCurrentMoodDisplay(todayEntries)

        // Update statistics
        updateStatistics(todayEntries.size, allEntries.size, averageIntensity, statistics)

        // Update mood history
        updateMoodHistory(todayEntries)
    }

    private fun updateCurrentMoodDisplay(todayEntries: List<MoodEntry>) {
        if (todayEntries.isNotEmpty()) {
            val latestMood = todayEntries.maxByOrNull { it.timestamp }
            latestMood?.let { mood ->
                binding.tvCurrentMoodEmoji.text = mood.mood.emoji
                binding.tvLastMoodTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(mood.date)
            }
        } else {
            binding.tvCurrentMoodEmoji.text = "😊"
            binding.tvLastMoodTime.text = "Never"
        }
    }

    private fun updateStatistics(
        todayCount: Int,
        totalCount: Int,
        averageIntensity: Double,
        statistics: Map<MoodType, Int>
    ) {
        binding.tvTodayMoods.text = todayCount.toString()
        binding.tvTotalMoods.text = totalCount.toString()
        binding.tvAvgIntensity.text = String.format("%.1f", averageIntensity)

        // Find most common mood
        val mostCommonMood = statistics.maxByOrNull { it.value }
        if (mostCommonMood != null && mostCommonMood.value > 0) {
            binding.tvMostCommonMoodEmoji.text = mostCommonMood.key.emoji
            binding.tvMostCommonMood.text = mostCommonMood.key.displayName
        } else {
            binding.tvMostCommonMoodEmoji.text = "😊"
            binding.tvMostCommonMood.text = "None"
        }
    }

    private fun updateMoodHistory(todayEntries: List<MoodEntry>) {
        if (todayEntries.isEmpty()) {
            binding.tvNoMoods.visibility = View.VISIBLE
            binding.rvMoodEntries.visibility = View.GONE
        } else {
            binding.tvNoMoods.visibility = View.GONE
            binding.rvMoodEntries.visibility = View.VISIBLE
            moodEntryAdapter.updateEntries(todayEntries)
        }
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
