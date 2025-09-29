package com.example.dailydose

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.dailydose.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val splashDelay = 3000L // 3 seconds
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide status bar for immersive experience
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars())
            window.insetsController?.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        
        startAnimations()
        
        // Navigate to appropriate activity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextActivity()
        }, splashDelay)
    }
    
    private fun navigateToNextActivity() {
        val sharedPreferences = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        val isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)
        
        val intent = if (isOnboardingCompleted) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        
        startActivity(intent)
        finish()
    }
    
    private fun startAnimations() {
        // Logo animation - scale and fade in
        binding.logoIcon.apply {
            scaleX = 0f
            scaleY = 0f
            alpha = 0f
        }
        
        ObjectAnimator.ofFloat(binding.logoIcon, "scaleX", 0f, 1.2f, 1f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        
        ObjectAnimator.ofFloat(binding.logoIcon, "scaleY", 0f, 1.2f, 1f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        
        ObjectAnimator.ofFloat(binding.logoIcon, "alpha", 0f, 1f).apply {
            duration = 800
            startDelay = 200
            start()
        }
        
        // Title animation - slide up and fade in
        binding.appTitle.apply {
            translationY = 100f
            alpha = 0f
        }
        
        ObjectAnimator.ofFloat(binding.appTitle, "translationY", 100f, 0f).apply {
            duration = 800
            startDelay = 600
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        
        ObjectAnimator.ofFloat(binding.appTitle, "alpha", 0f, 1f).apply {
            duration = 600
            startDelay = 600
            start()
        }
        
        // Subtitle animation - slide up and fade in
        binding.appSubtitle.apply {
            translationY = 80f
            alpha = 0f
        }
        
        ObjectAnimator.ofFloat(binding.appSubtitle, "translationY", 80f, 0f).apply {
            duration = 600
            startDelay = 1000
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        
        ObjectAnimator.ofFloat(binding.appSubtitle, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 1000
            start()
        }
        
        // Loading animation - fade in
        binding.loadingText.apply {
            alpha = 0f
        }
        
        ObjectAnimator.ofFloat(binding.loadingText, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 1500
            start()
        }
        
        // Progress bar animation - fade in
        binding.progressBar.apply {
            alpha = 0f
        }
        
        ObjectAnimator.ofFloat(binding.progressBar, "alpha", 0f, 1f).apply {
            duration = 500
            startDelay = 1800
            start()
        }
    }
}