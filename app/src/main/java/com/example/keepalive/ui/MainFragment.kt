package com.example.keepalive.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.keepalive.App
import com.example.keepalive.R
import com.example.keepalive.databinding.FragmentMainBinding
import com.example.keepalive.repository.Repository
import com.example.keepalive.repository.RepositoryImpl
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendPingNow.setOnClickListener { sendPlainPing() }
        binding.foregroundWorkerSettings.setOnClickListener { host.openForegroundWorkSettings() }
        binding.periodicWorkSettings.setOnClickListener { host.openPeriodicWorkSettings() }
    }

    private fun sendPlainPing() {
        lifecycleScope.launch {
            try {
                repository.sendPlainPing(
                    App.USER_ID,
                    "plain ping by button click"
                )
                toast("Success")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send ping", e)
                toast("Failed!!!")
            }
        }
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}