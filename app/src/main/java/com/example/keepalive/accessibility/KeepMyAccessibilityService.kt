package com.example.keepalive.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.keepalive.repository.Repository
import com.example.keepalive.repository.RepositoryImpl
import com.example.keepalive.storage.TelegramIdStorage
import kotlinx.coroutines.*

class KeepMyAccessibilityService : AccessibilityService() {

    private val scope = MainScope()

    private var totalRequests = 0
    private var failedRequests = 0

    override fun onCreate() {
        super.onCreate()
        scope.launch { startPolling() }
    }

    private suspend fun startPolling() {
        val userId = TelegramIdStorage(this).getUserId()
        if (userId <= 0L) {
            throw IllegalStateException()
        }
        val repository: Repository = RepositoryImpl()
        val timeoutInSeconds = 60L
        while (scope.isActive) {
            val text = "accessibility request #${++totalRequests}"
            val requestStartTime = SystemClock.elapsedRealtime()
            try {
                repository.sendLongPollingPing(userId, text, timeoutInSeconds)
            } catch (e: Exception) {
                if (!scope.isActive) {
                    break
                }
                Log.e(LOG_TAG, "Could not execute request", e)
                failedRequests++
                val millisSinceRequestStarted = SystemClock.elapsedRealtime() - requestStartTime
                val sleepTime = timeoutInSeconds * 1000 - millisSinceRequestStarted
                Log.i(LOG_TAG, "Sleeping $sleepTime ms.")
                delay(sleepTime)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        println("Event $event")
    }

    override fun onInterrupt() {
        println("Interrupted")
        scope.cancel()
    }

    companion object {
        private const val LOG_TAG = "KeepMyAccessibilityServ"
    }
}
