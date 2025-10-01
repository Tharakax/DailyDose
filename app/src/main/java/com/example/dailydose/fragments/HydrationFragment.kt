package com.example.dailydose.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dailydose.R
import com.example.dailydose.databinding.FragmentHydrationBinding
import com.example.dailydose.data.HealthRepository
import com.example.dailydose.model.HealthEntry
import com.example.dailydose.model.HealthType
import com.example.dailydose.services.WaterReminderService
import com.example.dailydose.utils.WaterAnimationHelper
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar

class HydrationFragment : Fragment() {

    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var healthRepository: HealthRepository
    private lateinit var waterReminderService: WaterReminderService
    private var currentWaterLevel = 0
    private val maxWaterLevel = 10
    private var totalWaterIntake = 0.0
    private val waterPerPress = 0.5 // 200ml per press
    private var hasJustReset = false
    private var selectedReminderInterval = 30 // Default to 30 minutes

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
            waterReminderService = WaterReminderService(requireContext())
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
        // Set initial water level with proper circular mask
        binding.waterLevel.clipToOutline = true
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

        binding.btnSetReminder.setOnClickListener {
            showReminderDialog()
        }

        binding.btnBackToDashboard.setOnClickListener {
            requireActivity().onBackPressed()
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
            updateWaterStats()

            // Show feedback
            showWaterFeedback()
        } else {
            Toast.makeText(context, "💧 You've reached your daily water goal! Great job!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun animateWaterFill() {
        val fillPercentage = (currentWaterLevel.toFloat() / maxWaterLevel) * 100f
        val targetScale = fillPercentage / 100f

        // Create circular fill animation using clip path
        val clipAnimator = ValueAnimator.ofFloat(binding.waterLevel.scaleY, targetScale)
        clipAnimator.duration = 800
        clipAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            binding.waterLevel.scaleY = animatedValue

            // Also animate glow to match
            binding.waterGlow.scaleY = animatedValue

            // Update water color based on level
            updateWaterColor(animatedValue)
        }

        // Create ripple effect
        val rippleAnimator = ObjectAnimator.ofFloat(binding.waterGlow, "alpha", 0.8f, 0.3f)
        rippleAnimator.duration = 600

        // Create bounce effect for the bubble
        val bounceX = ObjectAnimator.ofFloat(binding.waterBubble, "scaleX", 1f, 1.05f, 1f)
        val bounceY = ObjectAnimator.ofFloat(binding.waterBubble, "scaleY", 1f, 1.05f, 1f)
        bounceX.duration = 400
        bounceY.duration = 400

        // Start animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(clipAnimator, rippleAnimator, bounceX, bounceY)
        animatorSet.start()

        // Update percentage text with animation
        animatePercentageText(fillPercentage.toInt())

        // Start wave animation if water level is sufficient
        if (currentWaterLevel > 0) {
            startContinuousWaveAnimation()
        }
    }

    private fun updateWaterColor(scale: Float) {
        val colorIntensity = scale.coerceIn(0.3f, 1.0f)
        val waterColor = Color.argb(
            (255 * colorIntensity).toInt(),
            0,
            (150 * colorIntensity).toInt(),
            255
        )

        // Create a gradient or solid color based on water level
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.circle_water_fill)
        drawable?.setTint(waterColor)
        binding.waterLevel.background = drawable
    }

    private fun animatePercentageText(targetPercentage: Int) {
        val currentPercentage = binding.tvBubblePercentage.text.toString().replace("%", "").toIntOrNull() ?: 0

        val percentageAnimator = ValueAnimator.ofInt(currentPercentage, targetPercentage)
        percentageAnimator.duration = 600
        percentageAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            binding.tvBubblePercentage.text = "$animatedValue%"

            // Add scale animation to percentage text
            binding.tvBubblePercentage.scaleX = 1.1f
            binding.tvBubblePercentage.scaleY = 1.1f
            binding.tvBubblePercentage.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start()
        }
        percentageAnimator.start()
    }

    private fun updateWaterBubble() {
        val fillPercentage = (currentWaterLevel.toFloat() / maxWaterLevel) * 100f
        val scale = fillPercentage / 100f

        // Update water level visual
        binding.waterLevel.scaleY = scale
        binding.waterGlow.scaleY = scale

        // Update water color
        updateWaterColor(scale)

        // Update percentage text in bubble
        binding.tvBubblePercentage.text = "${fillPercentage.toInt()}%"

        // Special effects when full
        if (currentWaterLevel == maxWaterLevel) {
            celebrateGoalAchievement()
        }

        // Start or stop wave animation based on water level
        if (currentWaterLevel > 0) {
            startContinuousWaveAnimation()
        } else {
            binding.waterLevel.clearAnimation()
            binding.waterGlow.clearAnimation()
        }
    }

    private fun celebrateGoalAchievement() {
        // Create celebration animation
        val celebrateAnimator = AnimatorSet()

        val scaleX = ObjectAnimator.ofFloat(binding.waterBubble, "scaleX", 1f, 1.15f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.waterBubble, "scaleY", 1f, 1.15f, 1f)
        val rotation = ObjectAnimator.ofFloat(binding.waterBubble, "rotation", 0f, 5f, -5f, 0f)

        scaleX.duration = 600
        scaleY.duration = 600
        rotation.duration = 600

        celebrateAnimator.playTogether(scaleX, scaleY, rotation)
        celebrateAnimator.start()

        // Make percentage text pulse
        binding.tvBubblePercentage.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(300)
            .withEndAction {
                binding.tvBubblePercentage.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start()
            }
            .start()

        // Create simple celebration effect without particle container
        createCelebrationEffect()
    }

    private fun createCelebrationEffect() {
        // Simple celebration effect using existing views
        val confettiEmojis = listOf("🎉", "✨", "🌟", "💧", "⭐")

        // Animate the existing water glow for celebration
        val glowCelebration = ObjectAnimator.ofFloat(binding.waterGlow, "alpha", 0.3f, 0.8f, 0.3f)
        glowCelebration.duration = 1000
        glowCelebration.repeatCount = 3
        glowCelebration.start()

        // Create a simple confetti effect using multiple rapid animations
        confettiEmojis.forEachIndexed { index, emoji ->
            val confettiView = android.widget.TextView(requireContext()).apply {
                text = emoji
                textSize = 24f
                setShadowLayer(4f, 2f, 2f, Color.parseColor("#80000000"))
                x = binding.waterBubble.x + (Math.random() * binding.waterBubble.width).toFloat()
                y = binding.waterBubble.y - 100f
                alpha = 0f
                scaleX = 0f
                scaleY = 0f
            }

            // Add to the FrameLayout that contains the water bubble
            val frameLayout = binding.waterBubble.parent as? android.widget.FrameLayout
            frameLayout?.addView(confettiView)

            // Animate confetti falling using ObjectAnimator (Animator) instead of Animation
            val confettiAnimator = AnimatorSet()
            val fadeIn = ObjectAnimator.ofFloat(confettiView, "alpha", 0f, 1f)
            val fadeOut = ObjectAnimator.ofFloat(confettiView, "alpha", 1f, 0f)
            val scaleIn = ObjectAnimator.ofFloat(confettiView, "scaleX", 0f, 1.5f)
            val scaleOut = ObjectAnimator.ofFloat(confettiView, "scaleX", 1.5f, 0f)
            val scaleYIn = ObjectAnimator.ofFloat(confettiView, "scaleY", 0f, 1.5f)
            val scaleYOut = ObjectAnimator.ofFloat(confettiView, "scaleY", 1.5f, 0f)
            val fall = ObjectAnimator.ofFloat(confettiView, "y", confettiView.y, confettiView.y + 300f)
            val sway = ObjectAnimator.ofFloat(confettiView, "x",
                confettiView.x, confettiView.x + (Math.random() * 100 - 50).toFloat())

            confettiAnimator.playTogether(fadeIn, fadeOut, scaleIn, scaleOut, scaleYIn, scaleYOut, fall, sway)
            confettiAnimator.duration = 1500
            confettiAnimator.startDelay = index * 200L

            confettiAnimator.start()

            // Remove confetti after animation
            confettiAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    frameLayout?.removeView(confettiView)
                }
            })
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

                // Animate status text
                binding.tvWaterStatus.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(500)
                    .withEndAction {
                        binding.tvWaterStatus.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(500)
                            .start()
                    }
                    .start()
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

        // Animated toast appearance
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.view?.alpha = 0f
        toast.view?.animate()?.alpha(1f)?.duration = 300
        toast.show()
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

        // Create smooth drain animation
        val drainAnimator = ValueAnimator.ofFloat(binding.waterLevel.scaleY, 0f)
        drainAnimator.duration = 1000
        drainAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            binding.waterLevel.scaleY = animatedValue
            binding.waterGlow.scaleY = animatedValue
            updateWaterColor(animatedValue)
        }

        // Create ripple effect for drain
        val drainRipple = ObjectAnimator.ofFloat(binding.waterGlow, "alpha", 0.8f, 0f)
        drainRipple.duration = 800

        // Animate percentage text to 0
        animatePercentageText(0)

        val drainSet = AnimatorSet()
        drainSet.playTogether(drainAnimator, drainRipple)
        drainSet.start()

        // Stop wave animations
        binding.waterLevel.clearAnimation()
        binding.waterGlow.clearAnimation()

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
        // Animate water bubble entrance with fluid motion
        binding.waterBubble.alpha = 0f
        binding.waterBubble.scaleX = 0.8f
        binding.waterBubble.scaleY = 0.8f

        binding.waterBubble.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .withEndAction {
                // Start subtle wave animation after entrance if there's water
                if (currentWaterLevel > 0) {
                    startContinuousWaveAnimation()
                }
            }
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

        // Animate action buttons
        binding.btnResetWater.alpha = 0f
        binding.btnSetReminder.alpha = 0f
        binding.btnResetWater.translationY = 30f
        binding.btnSetReminder.translationY = 30f

        binding.btnResetWater.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(500)
            .start()

        binding.btnSetReminder.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(550)
            .start()
    }

    private fun startContinuousWaveAnimation() {
        // Only start wave animation if there's water in the bubble
        if (currentWaterLevel > 0) {
            // Use AnimationUtils for the water level (this is correct for View animations)
            val waveAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.water_wave_animation)
            binding.waterLevel.startAnimation(waveAnimation)

            // For the glow, create a separate animation with delay using ValueAnimator
            val glowAnimator = ValueAnimator.ofFloat(0f, 1f)
            glowAnimator.duration = 2000
            glowAnimator.repeatCount = ValueAnimator.INFINITE
            glowAnimator.repeatMode = ValueAnimator.REVERSE

            glowAnimator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.waterGlow.scaleX = 1.0f + value * 0.02f
                binding.waterGlow.scaleY = 1.0f + value * 0.02f
                binding.waterGlow.alpha = 0.3f + value * 0.4f
            }

            // Start glow animation after a delay
            binding.waterGlow.postDelayed({
                glowAnimator.start()
            }, 500)
        }
    }

    private fun showReminderDialog() {
        // Check notification permission first
        if (!hasNotificationPermission()) {
            requestNotificationPermission()
            return
        }

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_water_reminder, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Set up card click listeners
        val cards = listOf(
            dialogView.findViewById<androidx.cardview.widget.CardView>(R.id.card15min) to 15,
            dialogView.findViewById<androidx.cardview.widget.CardView>(R.id.card30min) to 30,
            dialogView.findViewById<androidx.cardview.widget.CardView>(R.id.card60min) to 60,
            dialogView.findViewById<androidx.cardview.widget.CardView>(R.id.card120min) to 120
        )

        cards.forEach { (card, minutes) ->
            card.setOnClickListener {
                // Reset all card backgrounds
                cards.forEach { (c, _) -> c.setCardBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent)) }

                // Highlight selected card
                card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_blue))
                selectedReminderInterval = minutes
            }
        }

        // Set default selection (30 minutes)
        dialogView.findViewById<androidx.cardview.widget.CardView>(R.id.card30min).setCardBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.light_blue)
        )

        // Set up button listeners
        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnSetReminder).setOnClickListener {
            waterReminderService.scheduleWaterReminder(selectedReminderInterval)
            val message = when (selectedReminderInterval) {
                15 -> "⚡ Quick reminders set every 15 minutes!"
                30 -> "⭐ Recommended reminders set every 30 minutes!"
                60 -> "🐌 Relaxed reminders set every hour!"
                120 -> "😴 Gentle reminders set every 2 hours!"
                else -> "Reminders set every $selectedReminderInterval minutes!"
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required for older versions
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show reminder dialog
                showReminderDialog()
            } else {
                Toast.makeText(context, "Notification permission is required for reminders", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}