package com.example.dailydose.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailydose.R
import com.example.dailydose.databinding.FragmentHydrationBinding
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthType
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

class HydrationFragment : Fragment() {

    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var healthRepository: HealthRepository
    private var currentWaterLevel = 0
    private val maxWaterLevel = 10
    private var totalWaterIntake = 0.0
    private val waterPerPress = 0.2 // 200ml per press
    private var hasJustReset = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHydrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            healthRepository = HealthRepository(requireContext())
            setupWaterBubble()
            setupClickListeners()
            loadTodayWaterIntake()
            startAnimations()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh water data when fragment becomes visible
        loadTodayWaterIntake()
    }

    private fun setupWaterBubble() {
        // Set initial water level
        updateWaterBubble()
        updateWaterStats()
    }

    private fun setupClickListeners() {
        binding.waterBubble.setOnClickListener {
            addWaterIntake()
        }

        binding.btnResetWater.setOnClickListener {
            resetWaterIntake()
        }
    }

    private fun addWaterIntake() {
        if (currentWaterLevel < maxWaterLevel) {
            currentWaterLevel++
            totalWaterIntake += waterPerPress

            // Animate water bubble fill
            animateWaterFill()

            // Save to repository
            saveWaterEntry()

            // Update UI
            updateWaterBubble()
            updateWaterStats()

            // Show feedback
            showWaterFeedback()
        } else {
            Toast.makeText(context, "💧 You've reached your daily water goal! Great job!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun animateWaterFill() {
        val fillPercentage = (currentWaterLevel.toFloat() / maxWaterLevel) * 100f

        // Animate the water level
        val animator = ObjectAnimator.ofFloat(binding.waterLevel, "scaleY",
            binding.waterLevel.scaleY, fillPercentage / 100f)
        animator.duration = 300
        animator.start()

        // Animate the water glow with the same scale
        val glowAnimator = ObjectAnimator.ofFloat(binding.waterGlow, "scaleY",
            binding.waterGlow.scaleY, fillPercentage / 100f)
        glowAnimator.duration = 300
        glowAnimator.start()

        // Animate the water color intensity
        val colorIntensity = (fillPercentage / 100f).coerceIn(0.3f, 1.0f)
        val waterColor = Color.argb(
            (255 * colorIntensity).toInt(),
            0, 150, 255
        )
        binding.waterLevel.setBackgroundColor(waterColor)

        // Update percentage text in bubble
        binding.tvBubblePercentage.text = "${fillPercentage.toInt()}%"
    }

    private fun updateWaterBubble() {
        val fillPercentage = (currentWaterLevel.toFloat() / maxWaterLevel) * 100f

        // Update water level visual
        binding.waterLevel.scaleY = fillPercentage / 100f

        // Update water glow to match
        binding.waterGlow.scaleY = fillPercentage / 100f

        // Update water color based on level
        val colorIntensity = (fillPercentage / 100f).coerceIn(0.3f, 1.0f)
        val waterColor = Color.argb(
            (255 * colorIntensity).toInt(),
            0, 150, 255
        )
        binding.waterLevel.setBackgroundColor(waterColor)

        // Update percentage text in bubble
        binding.tvBubblePercentage.text = "${fillPercentage.toInt()}%"

        // Update bubble animation
        if (currentWaterLevel == maxWaterLevel) {
            binding.waterBubble.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(200)
                .withEndAction {
                    binding.waterBubble.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(200)
                        .start()
                }
                .start()
        }
    }

    private fun updateWaterStats() {
        binding.tvWaterIntake.text = String.format("%.1f L", totalWaterIntake)
        binding.tvWaterLevel.text = "$currentWaterLevel/$maxWaterLevel"

        // Update progress bar
        val progress = (currentWaterLevel.toFloat() / maxWaterLevel * 100).toInt()
        binding.progressWater.progress = progress

        // Update progress percentage text
        binding.tvProgressPercentage.text = "$progress%"

        // Update status text
        when {
            currentWaterLevel == 0 -> {
                binding.tvWaterStatus.text = "💧 Start your hydration journey!"
                binding.tvWaterStatus.setTextColor(Color.parseColor("#666666"))
            }
            currentWaterLevel < maxWaterLevel / 2 -> {
                binding.tvWaterStatus.text = "💧 Keep going! You're doing great!"
                binding.tvWaterStatus.setTextColor(Color.parseColor("#4CAF50"))
            }
            currentWaterLevel < maxWaterLevel -> {
                binding.tvWaterStatus.text = "💧 Almost there! You're so close!"
                binding.tvWaterStatus.setTextColor(Color.parseColor("#2196F3"))
            }
            else -> {
                binding.tvWaterStatus.text = "🎉 Congratulations! Goal achieved!"
                binding.tvWaterStatus.setTextColor(Color.parseColor("#FF9800"))
            }
        }
    }

    private fun showWaterFeedback() {
        val feedbackMessages = listOf(
            "💧 Great! +200ml",
            "💧 Excellent! Keep it up!",
            "💧 Amazing! You're hydrated!",
            "💧 Fantastic! Stay healthy!",
            "💧 Wonderful! Keep drinking!",
            "💧 Superb! Almost there!",
            "💧 Incredible! So close!",
            "💧 Outstanding! One more!",
            "💧 Phenomenal! Last one!",
            "🎉 Perfect! Goal achieved!"
        )

        val message = feedbackMessages.getOrNull(currentWaterLevel - 1) ?: "💧 Great!"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveWaterEntry() {
        val entry = HealthEntry(
            id = UUID.randomUUID().toString(),
            type = HealthType.WATER,
            value = waterPerPress,
            unit = "L",
            date = Date(),
            notes = "Water intake - ${currentWaterLevel}/$maxWaterLevel"
        )

        healthRepository.saveHealthEntry(entry)
    }

    private fun loadTodayWaterIntake() {
        // If we just reset, don't reload from saved data
        if (hasJustReset) {
            hasJustReset = false
            updateWaterBubble()
            updateWaterStats()
            return
        }

        val todayEntries = healthRepository.getTodayEntries()
        val waterEntries = todayEntries.filter { it.type == HealthType.WATER }

        // Calculate total water intake from saved entries
        val savedWaterIntake = waterEntries.sumOf { it.value }
        val savedWaterLevel = (savedWaterIntake / waterPerPress).toInt().coerceAtMost(maxWaterLevel)

        // Update with saved data
        totalWaterIntake = savedWaterIntake
        currentWaterLevel = savedWaterLevel

        updateWaterBubble()
        updateWaterStats()
    }

    private fun resetWaterIntake() {
        currentWaterLevel = 0
        totalWaterIntake = 0.0
        hasJustReset = true

        // Clear all water entries from today
        clearTodayWaterEntries()

        // Animate reset for both water and glow
        binding.waterLevel.animate()
            .scaleY(0f)
            .setDuration(500)
            .start()

        binding.waterGlow.animate()
            .scaleY(0f)
            .setDuration(500)
            .withEndAction {
                updateWaterBubble()
                updateWaterStats()
            }
            .start()

        Toast.makeText(context, "💧 Water intake reset! Start fresh!", Toast.LENGTH_SHORT).show()
    }

    private fun clearTodayWaterEntries() {
        // Get all entries and remove today's water entries
        val allEntries = healthRepository.getAllHealthEntries()
        val filteredEntries = allEntries.filter { entry ->
            // Keep entry if it's not a water entry from today
            val isToday = isSameDay(entry.date, Date())
            val isWater = entry.type == HealthType.WATER
            !(isToday && isWater)
        }

        // Save the filtered entries back
        saveFilteredEntries(filteredEntries)

        // Also clear any cached data
        totalWaterIntake = 0.0
        currentWaterLevel = 0
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun saveFilteredEntries(entries: List<HealthEntry>) {
        val json = com.google.gson.Gson().toJson(entries)
        val sharedPreferences = requireContext().getSharedPreferences("health_data", android.content.Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("health_entries", json).apply()
    }

    private fun startAnimations() {
        // Animate water bubble entrance
        binding.waterBubble.alpha = 0f
        binding.waterBubble.animate()
            .alpha(1f)
            .setDuration(800)
            .start()

        // Animate stats grid
        binding.statsGrid.alpha = 0f
        binding.statsGrid.translationY = 50f
        binding.statsGrid.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(200)
            .start()

        // Animate progress card
        binding.cardProgress.alpha = 0f
        binding.cardProgress.translationY = 50f
        binding.cardProgress.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(300)
            .start()

        // Animate tips card
        binding.cardTips.alpha = 0f
        binding.cardTips.translationY = 50f
        binding.cardTips.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(400)
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}