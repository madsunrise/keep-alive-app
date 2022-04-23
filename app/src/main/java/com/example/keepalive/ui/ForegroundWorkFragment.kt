package com.example.keepalive.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.*
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.keepalive.App
import com.example.keepalive.R
import com.example.keepalive.databinding.FragmentForegroundWorkBinding
import com.example.keepalive.workmanager.LongRunningWorker

class ForegroundWorkFragment : Fragment(R.layout.fragment_foreground_work) {

    private val binding: FragmentForegroundWorkBinding by viewBinding(FragmentForegroundWorkBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStatus()
        binding.scheduleLongRunningWorker.setOnClickListener { schedule() }
        binding.cancelWorker.setOnClickListener { cancel() }
    }

    private fun observeStatus() {
        val wm = WorkManager.getInstance(requireContext())
        wm.getWorkInfosByTagLiveData(LongRunningWorker.TAG).observe(viewLifecycleOwner) { infoList ->
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
        val request = OneTimeWorkRequest.Builder(LongRunningWorker::class.java)
            .setInputData(
                workDataOf(
                    LongRunningWorker.KEY_USER_ID to App.USER_ID,
                    LongRunningWorker.KEY_LONG_POLLING_TIMEOUT to LONG_POLLING_TIMEOUT
                )
            )
            .addTag(LongRunningWorker.TAG)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            LongRunningWorker.TAG,
            ExistingWorkPolicy.KEEP,
            request
        )
        Log.i(LOG_TAG, "Foreground work has been started")
    }

    private fun cancel() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(LongRunningWorker.TAG)
        Log.i(LOG_TAG, "All works have been cancelled")
    }

    companion object {
        private const val LOG_TAG = "ForegroundWorkFragment"
        private const val LONG_POLLING_TIMEOUT = 60L
    }
}