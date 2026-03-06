package com.example.asltranslator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.asltranslator.databinding.ActivityMainBinding
import androidx.navigation.ui.onNavDestinationSelected

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar configuration
        setSupportActionBar(binding.toolbar)

        // navigation configuration
        val navController = findNavController(R.id.nav_host_fragment)
        binding.toolbar.setupWithNavController(navController)

        // notifications channel
        createNotificationChannel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        // NavigationUI automatically links menu ID with the fragment ID
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Gesture translation"
            val descriptionText = "Notitifcations for ASL"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ASL_NOTIF", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}