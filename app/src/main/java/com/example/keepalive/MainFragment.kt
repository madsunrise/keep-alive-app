package com.example.keepalive

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.keepalive.databinding.FragmentMainBinding
import com.example.keepalive.repository.Repository
import com.example.keepalive.repository.RepositoryImpl
import com.example.keepalive.workmanager.LongRunningWorker
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding: FragmentMainBinding by viewBinding(FragmentMainBinding::bind)

    private val repository: Repository = RepositoryImpl()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sendPingNow.setOnClickListener { sendPlainPing() }
        binding.launchLongRunningWorker.setOnClickListener { launchLongRunningWorker() }
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

    private fun launchLongRunningWorker() {
        val request = OneTimeWorkRequest.Builder(LongRunningWorker::class.java)
            .setInputData(
                workDataOf(
                    LongRunningWorker.KEY_USER_ID to App.USER_ID,
                    LongRunningWorker.KEY_LONG_POLLING_TIMEOUT to LONG_POLLING_TIMEOUT
                )
            )
            .build()
        WorkManager.getInstance(requireContext()).enqueue(request)
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MainFragment"
        private const val LONG_POLLING_TIMEOUT = 60L
    }
}