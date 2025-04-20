package com.tictac.moviesapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.tictac.moviesapp.R
import com.tictac.moviesapp.databinding.IntroScreenBinding

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: IntroScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IntroScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start animations
        animateIntroScreenElements()

        // Set up button click
        binding.getStartedButton.setOnClickListener {
            navigateToMainActivity()
        }

        // Automatically proceed after 10 seconds if user doesn't click button
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                navigateToMainActivity()
            }
        }, 10000)
    }

    private fun animateIntroScreenElements() {
        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        // Set initial visibility
        binding.logoImage.visibility = View.INVISIBLE
        binding.appTitle.visibility = View.INVISIBLE
        binding.appSubtitle.visibility = View.INVISIBLE
        binding.getStartedButton.visibility = View.INVISIBLE

        // Start animations with delays
        binding.logoImage.postDelayed({
            binding.logoImage.visibility = View.VISIBLE
            binding.logoImage.startAnimation(fadeIn)
        }, 300)

        binding.appTitle.postDelayed({
            binding.appTitle.visibility = View.VISIBLE
            binding.appTitle.startAnimation(slideUp)
        }, 600)

        binding.appSubtitle.postDelayed({
            binding.appSubtitle.visibility = View.VISIBLE
            binding.appSubtitle.startAnimation(slideUp)
        }, 900)

        binding.getStartedButton.postDelayed({
            binding.getStartedButton.visibility = View.VISIBLE
            binding.getStartedButton.startAnimation(fadeIn)
        }, 1200)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close the intro activity
    }
}