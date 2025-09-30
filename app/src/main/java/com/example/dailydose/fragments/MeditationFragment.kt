package com.example.dailydose.fragments

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dailydose.R
import com.example.dailydose.databinding.FragmentMeditationBinding
import java.util.concurrent.TimeUnit

class MeditationFragment : Fragment() {

    private var _binding: FragmentMeditationBinding? = null
    private val binding get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private var countDownTimer: CountDownTimer? = null
    private var isMeditationActive = false
    private var selectedDuration = 5 // Default 5 minutes
    private var isMusicEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMeditationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
        updateUI()
    }

    private fun setupUI() {
        // Set up duration selection
        binding.rgDuration.setOnCheckedChangeListener { _, checkedId ->
            selectedDuration = when (checkedId) {
                R.id.rb_1min -> 1
                R.id.rb_5min -> 5
                R.id.rb_10min -> 10
                R.id.rb_custom -> {
                    // For custom duration, we'll use 15 minutes as default
                    // In a real app, you'd show a dialog to let user input custom time
                    15
                }
                else -> 5
            }
            updateDurationDisplay()
        }

        // Set up music toggle
        binding.switchMusic.setOnCheckedChangeListener { _, isChecked ->
            isMusicEnabled = isChecked
            updateMusicDisplay()
        }

        // Initialize with default values
        binding.rb5min.isChecked = true
        updateDurationDisplay()
        updateMusicDisplay()
    }

    private fun setupClickListeners() {
        // Back button to dashboard
        binding.btnBackToDashboard.setOnClickListener {
            navigateToDashboard()
        }

        binding.btnStartMeditation.setOnClickListener {
            startMeditation()
        }

        binding.btnStopMeditation.setOnClickListener {
            stopMeditation()
        }

        binding.btnCustomDuration.setOnClickListener {
            showCustomDurationDialog()
        }
    }

    private fun startMeditation() {
        if (isMeditationActive) return

        isMeditationActive = true
        updateUI()

        // Start music if enabled
        if (isMusicEnabled) {
            startBackgroundMusic()
        }

        // Start countdown timer
        startCountdownTimer(selectedDuration * 60 * 1000L) // Convert minutes to milliseconds

        Toast.makeText(context, "Meditation started. Find your inner peace.", Toast.LENGTH_SHORT).show()
    }

    private fun stopMeditation() {
        if (!isMeditationActive) return

        isMeditationActive = false
        updateUI()

        // Stop music
        stopBackgroundMusic()

        // Stop countdown timer
        countDownTimer?.cancel()
        countDownTimer = null

        Toast.makeText(context, "Meditation session ended.", Toast.LENGTH_SHORT).show()
    }

    private fun startCountdownTimer(durationMs: Long) {
        countDownTimer = object : CountDownTimer(durationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.tvCountdown.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvCountdown.text = "00:00"
                stopMeditation()
                Toast.makeText(context, "Meditation session completed! Well done.", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun startBackgroundMusic() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, R.raw.meditation_music)
            mediaPlayer?.apply {
                isLooping = true
                setVolume(0.3f, 0.3f) // Lower volume for background music
                start()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not play background music", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopBackgroundMusic() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }

    private fun updateUI() {
        if (isMeditationActive) {
            binding.meditationSetup.visibility = View.GONE
            binding.meditationActive.visibility = View.VISIBLE
            binding.btnStartMeditation.visibility = View.GONE
            binding.btnStopMeditation.visibility = View.VISIBLE
        } else {
            binding.meditationSetup.visibility = View.VISIBLE
            binding.meditationActive.visibility = View.GONE
            binding.btnStartMeditation.visibility = View.VISIBLE
            binding.btnStopMeditation.visibility = View.GONE
            binding.tvCountdown.text = String.format("%02d:%02d", selectedDuration, 0)
        }
    }

    private fun updateDurationDisplay() {
        val durationText = when (selectedDuration) {
            1 -> "1 minute"
            5 -> "5 minutes"
            10 -> "10 minutes"
            15 -> "15 minutes (Custom)"
            else -> "$selectedDuration minutes"
        }
        binding.tvSelectedDuration.text = "Selected: $durationText"
        binding.tvCountdown.text = String.format("%02d:%02d", selectedDuration, 0)
    }

    private fun updateMusicDisplay() {
        val musicText = if (isMusicEnabled) "Background music enabled" else "Silent meditation"
        binding.tvMusicStatus.text = musicText
    }

    private fun showCustomDurationDialog() {
        // In a real app, you would show a dialog for custom duration input
        // For now, we'll just cycle through some preset options
        selectedDuration = when (selectedDuration) {
            15 -> 20
            20 -> 30
            30 -> 15
            else -> 15
        }
        updateDurationDisplay()
        Toast.makeText(context, "Custom duration set to $selectedDuration minutes", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMeditation()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        // Pause meditation when fragment is paused
        if (isMeditationActive) {
            stopMeditation()
        }
    }

    private fun navigateToDashboard() {
        try {
            val navController = findNavController()
            navController.navigate(R.id.dashboardFragment)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Navigation error", Toast.LENGTH_SHORT).show()
        }
    }
}

