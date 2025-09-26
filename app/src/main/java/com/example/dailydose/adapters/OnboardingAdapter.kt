package com.example.dailydose.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dailydose.fragments.OnboardingWelcomeFragment
import com.example.dailydose.fragments.OnboardingHealthFragment
import com.example.dailydose.fragments.OnboardingTasksFragment
import com.example.dailydose.fragments.OnboardingProgressFragment

class OnboardingAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingWelcomeFragment()
            1 -> OnboardingHealthFragment()
            2 -> OnboardingTasksFragment()
            3 -> OnboardingProgressFragment()
            else -> OnboardingWelcomeFragment()
        }
    }
}
