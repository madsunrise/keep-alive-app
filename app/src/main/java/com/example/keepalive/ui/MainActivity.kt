package com.example.keepalive.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.keepalive.R
import com.example.keepalive.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main), Host {

    private val binding: ActivityMainBinding by viewBinding(
        ActivityMainBinding::bind,
        R.id.container
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(binding.fragmentContainer.id, MainFragment())
                .commit()
        }
    }

    override fun openPeriodicWorkSettings() {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, PeriodicWorkFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun openForegroundWorkSettings() {
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, ForegroundWorkFragment())
            .addToBackStack(null)
            .commit()
    }
}