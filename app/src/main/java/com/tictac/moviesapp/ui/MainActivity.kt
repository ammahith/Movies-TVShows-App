package com.tictac.moviesapp.ui

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.tictac.moviesapp.R
import com.tictac.moviesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Store scroll positions for main fragments
    private val scrollPositions = mutableMapOf<Int, Int>()

    // Store RecyclerView states
    private val recyclerViewStates = mutableMapOf<String, Parcelable?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        // Home tab (Movies)
        binding.navHome.setOnClickListener {
            navController.navigate(R.id.navigation_home)
            updateNavigationUI(0)
        }

        // Dashboard tab (TV Shows)
        binding.navDashboard.setOnClickListener {
            navController.navigate(R.id.navigation_dashboard)
            updateNavigationUI(1)
        }

        // Notifications tab (People)
        binding.navNotifications.setOnClickListener {
            navController.navigate(R.id.navigation_notifications)
            updateNavigationUI(2)
        }

        // Set initial state
        updateNavigationUI(0)
    }

    private fun updateNavigationUI(selectedIndex: Int) {
        // Reset all text colors
        binding.tvHomeLabel.setTextColor(getColor(R.color.nav_item_inactive))
        binding.tvDashboardLabel.setTextColor(getColor(R.color.nav_item_inactive))
        binding.tvNotificationsLabel.setTextColor(getColor(R.color.nav_item_inactive))

        // Reset all icon tints
        binding.ivHomeIcon.setColorFilter(getColor(R.color.nav_item_inactive))
        binding.ivDashboardIcon.setColorFilter(getColor(R.color.nav_item_inactive))
        binding.ivNotificationsIcon.setColorFilter(getColor(R.color.nav_item_inactive))

        // Set active item
        when (selectedIndex) {
            0 -> {
                binding.tvHomeLabel.setTextColor(getColor(R.color.nav_item_active))
                binding.ivHomeIcon.setColorFilter(getColor(R.color.nav_item_active))
            }
            1 -> {
                binding.tvDashboardLabel.setTextColor(getColor(R.color.nav_item_active))
                binding.ivDashboardIcon.setColorFilter(getColor(R.color.nav_item_active))
            }
            2 -> {
                binding.tvNotificationsLabel.setTextColor(getColor(R.color.nav_item_active))
                binding.ivNotificationsIcon.setColorFilter(getColor(R.color.nav_item_active))
            }
        }
    }

    // Helper methods for scroll position management
    fun saveScrollPosition(fragmentId: Int, position: Int) {
        scrollPositions[fragmentId] = position
    }

    fun getScrollPosition(fragmentId: Int): Int {
        return scrollPositions[fragmentId] ?: 0
    }

    // Helper methods for RecyclerView state management
    fun saveRecyclerViewState(key: String, state: Parcelable?) {
        recyclerViewStates[key] = state
    }

    fun getRecyclerViewState(key: String): Parcelable? {
        return recyclerViewStates[key]
    }
}