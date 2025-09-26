package com.example.dailydose

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.dailydose.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()
        navigateToMainActivity()
    }

    private fun startAnimations() {
        // Animate logo
        binding.ivLogo?.let { logo ->
            val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
            logo.startAnimation(bounceIn)
        }

        // Animate app name
        binding.tvAppName?.let { appName ->
            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            fadeIn.startOffset = 300
            appName.startAnimation(fadeIn)
        }

        // Animate tagline
        binding.tvTagline?.let { tagline ->
            val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom)
            slideIn.startOffset = 600
            tagline.startAnimation(slideIn)
        }

        // Animate progress bar
        binding.progressBar?.let { progress ->
            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            fadeIn.startOffset = 900
            progress.startAnimation(fadeIn)
        }
    }

    private fun navigateToMainActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500) // 2.5 seconds splash screen
    }
}
