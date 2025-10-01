package com.example.dailydose.utils

import android.view.View
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator

/**
 * Demo class to showcase the fluid animation capabilities
 * This can be used for testing and demonstration purposes
 */
class FluidAnimationDemo {
    
    companion object {
        
        /**
         * Demonstrates all fluid animation types in sequence
         * Useful for testing the animation system
         */
        fun demonstrateAllAnimations(waterView: View, onComplete: (() -> Unit)? = null) {
            val animatorSet = AnimatorSet()
            val animations = mutableListOf<android.animation.Animator>()
            
            // 1. Fluid fill animation
            val fluidFill = WaterAnimationHelper.createFluidFillAnimation(waterView, 0.5f, 1000L)
            animations.add(fluidFill)
            
            // 2. Ripple effect
            val ripple = WaterAnimationHelper.createRippleEffect(waterView)
            animations.add(ripple)
            
            // 3. Settling animation
            val settling = WaterAnimationHelper.createSettlingAnimation(waterView)
            animations.add(settling)
            
            // 4. Splash effect
            val splash = WaterAnimationHelper.createSplashEffect(waterView)
            animations.add(splash)
            
            // Play animations in sequence
            animatorSet.playSequentially(animations)
            
            onComplete?.let { callback ->
                animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        callback()
                    }
                })
            }
            
            animatorSet.start()
        }
        
        /**
         * Creates a continuous wave effect for demonstration
         */
        fun createContinuousWaveDemo(waterView: View, duration: Long = 5000L) {
            val waveAnimator = ValueAnimator.ofFloat(0f, 1f)
            waveAnimator.duration = duration
            waveAnimator.repeatCount = ValueAnimator.INFINITE
            waveAnimator.repeatMode = ValueAnimator.REVERSE
            waveAnimator.interpolator = AccelerateDecelerateInterpolator()
            
            waveAnimator.addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val waveOffset = (kotlin.math.sin(progress * Math.PI * 4) * 3).toFloat()
                waterView.translationY = waveOffset
                
                val waveScale = 1.0f + (kotlin.math.sin(progress * Math.PI * 2) * 0.02f).toFloat()
                waterView.scaleX = waveScale
                waterView.scaleY = waveScale
            }
            
            waveAnimator.start()
        }
        
        /**
         * Creates a dramatic fill animation for demonstration
         */
        fun createDramaticFillDemo(waterView: View, onComplete: (() -> Unit)? = null) {
            val animatorSet = AnimatorSet()
            
            // Start with empty
            waterView.scaleY = 0f
            
            // Create dramatic fill with multiple stages
            val stage1 = ObjectAnimator.ofFloat(waterView, "scaleY", 0f, 0.3f)
            stage1.duration = 500
            stage1.interpolator = BounceInterpolator()
            
            val stage2 = ObjectAnimator.ofFloat(waterView, "scaleY", 0.3f, 0.7f)
            stage2.duration = 800
            stage2.interpolator = OvershootInterpolator(1.5f)
            
            val stage3 = ObjectAnimator.ofFloat(waterView, "scaleY", 0.7f, 1.0f)
            stage3.duration = 600
            stage3.interpolator = AccelerateDecelerateInterpolator()
            
            // Add ripple effects between stages
            val ripple1 = WaterAnimationHelper.createRippleEffect(waterView)
            val ripple2 = WaterAnimationHelper.createRippleEffect(waterView)
            
            animatorSet.playSequentially(
                stage1,
                ripple1,
                stage2,
                ripple2,
                stage3
            )
            
            onComplete?.let { callback ->
                animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        callback()
                    }
                })
            }
            
            animatorSet.start()
        }
    }
}
