package com.example.asltranslator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatDelegate
import android.widget.ImageView
import com.example.asltranslator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        // load saved theme, default to Dark Mode (MODE_NIGHT_YES) if not set
        val savedMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_YES)
        AppCompatDelegate.setDefaultNightMode(savedMode)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar configuration
        setSupportActionBar(binding.toolbar)

        // navigation configuration
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment, R.id.cameraFragment, R.id.galleryFragment,
            R.id.lessonsStudyFragment, R.id.lessonsQuizFragment
        ))
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        val btnThemeToggle = findViewById<ImageView>(R.id.btn_theme_toggle)
        btnThemeToggle?.setOnClickListener {
            // read from SharedPreferences to know the current forced state
            val currentMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_YES)
            val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            
            prefs.edit().putInt("night_mode", newMode).apply()
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }
}