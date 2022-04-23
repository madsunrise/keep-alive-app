package com.example.keepalive.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
        replaceFragment(PeriodicWorkFragment())
    }

    override fun openForegroundWorkSettings() {
        replaceFragment(ForegroundWorkFragment())
    }

    override fun openSettingsFragment() {
        replaceFragment(SettingsFragment())
    }

    override fun openPrevious() {
        supportFragmentManager.popBackStack()
    }

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}