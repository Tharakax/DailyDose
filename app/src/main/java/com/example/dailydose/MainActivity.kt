package com.example.dailydose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dailydose.databinding.ActivityMainBinding
import android.view.animation.AnimationUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Enable edge-to-edge display
            WindowCompat.setDecorFitsSystemWindows(window, false)
            
            setupNavigation()
            startAnimations()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error gracefully
        }
    }

    private fun setupNavigation() {
        try {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
            val navController = navHostFragment?.navController

            if (navController != null) {
                binding.bottomNavigation.setupWithNavController(navController)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startAnimations() {
        // Animate bottom navigation
        binding.bottomNavigation?.let { nav ->
            val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_bottom)
            slideIn.startOffset = 500
            nav.startAnimation(slideIn)
        }
    }
}