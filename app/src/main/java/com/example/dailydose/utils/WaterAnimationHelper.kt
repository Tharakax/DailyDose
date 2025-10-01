package com.example.dailydose.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import kotlin.math.sin

class WaterAnimationHelper {
    
    companion object {
        fun createFluidFillAnimation(
            waterView: View,
            targetScale: Float,
            duration: Long = 800L,
            onAnimationEnd: (() -> Unit)? = null
        ): AnimatorSet {
            val animatorSet = AnimatorSet()
            
            // Main scale animation with fluid motion
            val scaleAnimator = ObjectAnimator.ofFloat(waterView, "scaleY", waterView.scaleY, targetScale)
            scaleAnimator.duration = duration
            scaleAnimator.interpolator = AccelerateDecelerateInterpolator()
            
            // Add subtle wave motion during fill
            val waveAnimator = createWaveAnimation(waterView, duration)
            
            // Combine animations
            animatorSet.playTogether(scaleAnimator, waveAnimator)
            
            onAnimationEnd?.let { callback ->
                animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        callback()
                    }
                })
            }
            
            return animatorSet
        }
        
        fun createRippleEffect(
            waterView: View,
            onAnimationEnd: (() -> Unit)? = null
        ): AnimatorSet {
            val animatorSet = AnimatorSet()
            
            // Ripple scale animation
            val rippleScale = ObjectAnimator.ofFloat(waterView, "scaleX", 1.0f, 1.15f, 1.0f)
            rippleScale.duration = 300
            rippleScale.interpolator = OvershootInterpolator(1.2f)
            
            val rippleScaleY = ObjectAnimator.ofFloat(waterView, "scaleY", 1.0f, 1.15f, 1.0f)
            rippleScaleY.duration = 300
            rippleScaleY.interpolator = OvershootInterpolator(1.2f)
            
            // Alpha pulse
            val alphaPulse = ObjectAnimator.ofFloat(waterView, "alpha", 1.0f, 0.7f, 1.0f)
            alphaPulse.duration = 300
            
            animatorSet.playTogether(rippleScale, rippleScaleY, alphaPulse)
            
            onAnimationEnd?.let { callback ->
                animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        callback()
                    }
                })
            }
            
            return animatorSet
        }
        
        fun createSettlingAnimation(
            waterView: View,
            onAnimationEnd: (() -> Unit)? = null
        ): AnimatorSet {
            val animatorSet = AnimatorSet()
            
            // Subtle bounce effect to simulate water settling
            val settleBounce = ObjectAnimator.ofFloat(waterView, "scaleY", waterView.scaleY, waterView.scaleY * 0.98f, waterView.scaleY)
            settleBounce.duration = 400
            settleBounce.interpolator = BounceInterpolator()
            
            // Gentle wave motion
            val waveMotion = createWaveAnimation(waterView, 600L)
            
            animatorSet.playTogether(settleBounce, waveMotion)
            
            onAnimationEnd?.let { callback ->
                animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        callback()
                    }
                })
            }
            
            return animatorSet
        }
        
        private fun createWaveAnimation(waterView: View, duration: Long): ValueAnimator {
            val waveAnimator = ValueAnimator.ofFloat(0f, 1f)
            waveAnimator.duration = duration
            waveAnimator.repeatCount = ValueAnimator.INFINITE
            waveAnimator.repeatMode = ValueAnimator.REVERSE
            
            waveAnimator.addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val waveOffset = (sin(progress * Math.PI * 2) * 2).toFloat() // Subtle wave motion
                waterView.translationY = waveOffset
            }
            
            return waveAnimator
        }
        
        fun createFluidColorTransition(
            waterView: View,
            startColor: Int,
            endColor: Int,
            duration: Long = 500L
        ): ValueAnimator {
            val colorAnimator = ValueAnimator.ofFloat(0f, 1f)
            colorAnimator.duration = duration
            colorAnimator.interpolator = AccelerateDecelerateInterpolator()
            
            colorAnimator.addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val interpolatedColor = interpolateColor(startColor, endColor, progress)
                waterView.setBackgroundColor(interpolatedColor)
            }
            
            return colorAnimator
        }
        
        private fun interpolateColor(startColor: Int, endColor: Int, progress: Float): Int {
            val startA = (startColor shr 24) and 0xFF
            val startR = (startColor shr 16) and 0xFF
            val startG = (startColor shr 8) and 0xFF
            val startB = startColor and 0xFF
            
            val endA = (endColor shr 24) and 0xFF
            val endR = (endColor shr 16) and 0xFF
            val endG = (endColor shr 8) and 0xFF
            val endB = endColor and 0xFF
            
            val currentA = (startA + (endA - startA) * progress).toInt()
            val currentR = (startR + (endR - startR) * progress).toInt()
            val currentG = (startG + (endG - startG) * progress).toInt()
            val currentB = (startB + (endB - startB) * progress).toInt()
            
            return (currentA shl 24) or (currentR shl 16) or (currentG shl 8) or currentB
        }
        
        fun createSplashEffect(
            waterView: View,
            onAnimationEnd: (() -> Unit)? = null
        ): AnimatorSet {
            val animatorSet = AnimatorSet()
            
            // Quick scale up and down for splash effect
            val splashScale = ObjectAnimator.ofFloat(waterView, "scaleX", 1.0f, 1.2f, 1.0f)
            splashScale.duration = 200
            
            val splashScaleY = ObjectAnimator.ofFloat(waterView, "scaleY", 1.0f, 1.2f, 1.0f)
            splashScaleY.duration = 200
            
            // Rotation for dynamic effect
            val splashRotation = ObjectAnimator.ofFloat(waterView, "rotation", 0f, 5f, -5f, 0f)
            splashRotation.duration = 200
            
            // Alpha flash
            val alphaFlash = ObjectAnimator.ofFloat(waterView, "alpha", 1.0f, 0.5f, 1.0f)
            alphaFlash.duration = 200
            
            animatorSet.playTogether(splashScale, splashScaleY, splashRotation, alphaFlash)
            
            onAnimationEnd?.let { callback ->
                animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        callback()
                    }
                })
            }
            
            return animatorSet
        }
    }
}
