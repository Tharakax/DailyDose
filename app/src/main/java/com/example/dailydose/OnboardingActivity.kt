package com.example.dailydose

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.dailydose.adapters.OnboardingAdapter
import com.example.dailydose.databinding.ActivityOnboardingBinding
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupClickListeners()
        updatePageIndicator()
    }

    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter(this)
        binding.viewPager.adapter = onboardingAdapter
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updatePageIndicator()
                updateNavigationButtons()
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnSkip.setOnClickListener {
            navigateToMainActivity()
        }

        binding.btnNext.setOnClickListener {
            if (currentPage < onboardingAdapter.itemCount - 1) {
                binding.viewPager.currentItem = currentPage + 1
            } else {
                navigateToMainActivity()
            }
        }

        binding.btnBack.setOnClickListener {
            if (currentPage > 0) {
                binding.viewPager.currentItem = currentPage - 1
            }
        }
    }

    private fun updatePageIndicator() {
        // Update page indicator dots
        binding.dot1.isSelected = currentPage == 0
        binding.dot2.isSelected = currentPage == 1
        binding.dot3.isSelected = currentPage == 2
        binding.dot4.isSelected = currentPage == 3
    }

    private fun updateNavigationButtons() {
        binding.btnBack.visibility = if (currentPage == 0) android.view.View.GONE else android.view.View.VISIBLE
        
        if (currentPage == onboardingAdapter.itemCount - 1) {
            binding.btnNext.text = "Get Started"
        } else {
            binding.btnNext.text = "Next"
        }
    }

    private fun navigateToMainActivity() {
        // Mark onboarding as completed
        val sharedPreferences = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("onboarding_completed", true).apply()
        
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

