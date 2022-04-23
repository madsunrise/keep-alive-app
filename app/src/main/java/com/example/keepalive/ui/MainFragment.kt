package com.example.keepalive.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.keepalive.R
import com.example.keepalive.databinding.FragmentMainBinding
import com.example.keepalive.repository.Repository
import com.example.keepalive.repository.RepositoryImpl
import com.example.keepalive.storage.TelegramIdStorage
import com.example.keepalive.utils.Extensions.toast
import kotlinx.coroutines.launch
import kotlin.properties.Delegates.notNull

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)
    private val repository: Repository = RepositoryImpl()
    private var host: Host by notNull()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        host = context as Host
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        lifecycleScope.launch { validateUserId() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendPingNow.setOnClickListener { sendPlainPing() }
        binding.foregroundWorkerSettings.setOnClickListener { openForegroundWorkerSettings() }
        binding.periodicWorkSettings.setOnClickListener { openPeriodicWorkSettings() }
    }

    private fun sendPlainPing() {
        lifecycleScope.launch {
            try {
                if (!validateUserId()) {
                    return@launch
                }
                repository.sendPlainPing(
                    TelegramIdStorage(requireContext()).getUserId(),
                    "ping by button click"
                )
                toast("Success")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send ping", e)
                toast("Failed!!!")
            }
        }
    }

    private fun openForegroundWorkerSettings() {
        lifecycleScope.launch {
            if (!validateUserId()) {
                return@launch
            }
            host.openForegroundWorkSettings()
        }
    }

    private fun openPeriodicWorkSettings() {
        lifecycleScope.launch {
            if (!validateUserId()) {
                return@launch
            }
            host.openPeriodicWorkSettings()
        }
    }

    private suspend fun validateUserId(): Boolean {
        val storage = TelegramIdStorage(requireContext())
        return if (!storage.containsUserId()) {
            Log.i(TAG, "Validation failed, open settings fragment")
            toast("You need to set your ID first", Toast.LENGTH_LONG)
            host.openSettingsFragment()
            false
        } else {
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.main_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                host.openSettingsFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}