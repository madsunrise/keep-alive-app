package com.example.keepalive.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.keepalive.R
import com.example.keepalive.databinding.FragmentPeriodicWorkBinding
import com.example.keepalive.storage.TelegramIdStorage
import com.example.keepalive.workmanager.LongRunningWorker
import com.example.keepalive.workmanager.PlainWorker
import kotlinx.coroutines.launch
import java.time.Duration

class PeriodicWorkFragment : Fragment(R.layout.fragment_periodic_work) {

    private val binding: FragmentPeriodicWorkBinding by viewBinding(FragmentPeriodicWorkBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStatus()
        binding.schedulePeriodicWork.setOnClickListener { schedule() }
        binding.cancelPeriodicWork.setOnClickListener { cancel() }
    }

    private fun observeStatus() {
        val wm = WorkManager.getInstance(requireContext())
        wm.getWorkInfosByTagLiveData(PlainWorker.TAG).observe(viewLifecycleOwner) { infoList ->
            Log.i(LOG_TAG, "Updated status: ${infoList.joinToString { it.state.name }}")
            val text = when {
                infoList.isEmpty() -> "Current status will be displayed here"
                infoList.size == 1 -> infoList.first().state.name
                else -> {
                    val sb = StringBuilder()
                    for ((i, info) in infoList.withIndex()) {
                        sb.append(i).append(". ").append(info.state.name).append('\n')
                    }
                    sb.toString().trim()
                }
            }
           binding.currentStatus.text = text
        }
    }

    private fun schedule() {
        lifecycleScope.launch {
            val userId = TelegramIdStorage(requireContext()).getUserId()
            val request = PeriodicWorkRequestBuilder<PlainWorker>(PERIOD)
                .addTag(PlainWorker.TAG)
                .setInputData(
                    workDataOf(
                        LongRunningWorker.KEY_USER_ID to userId
                    )
                )
                .build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                PlainWorker.TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
            Log.i(LOG_TAG, "Periodic work has been scheduled")
        }
    }

    private fun cancel() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(PlainWorker.TAG)
        Log.i(LOG_TAG, "All works have been cancelled")
    }

    companion object {
        private val PERIOD = Duration.ofMinutes(15L)
        private const val LOG_TAG = "PeriodicWorkFragment"
    }
}