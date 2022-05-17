package com.example.keepalive.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.example.keepalive.repository.Repository
import com.example.keepalive.repository.RepositoryImpl
import com.example.keepalive.storage.TelegramIdStorage
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates.notNull

class BackgroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    private var thread: MyThread by notNull()

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service onCreate")
        val userId = runBlocking { TelegramIdStorage(this@BackgroundService).getUserId() }
        thread = MyThread(userId)
        thread.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service onStartCommand")
        return START_STICKY
    }

    private class MyThread(private val userId: Long) : Thread() {
        private var totalRequests = 0
        private var failedRequests = 0
        private val repository: Repository = RepositoryImpl()

        override fun run() {
            while (!isInterrupted) {
                val text =
                    "Request from service #${++totalRequests}, failed requests: $failedRequests"
                val requestStartTime = SystemClock.elapsedRealtime()
                try {
                    runBlocking { repository.sendLongPollingPing(userId, text, TIMEOUT_SEC) }
                } catch (e: Exception) {
                    if (isInterrupted) {
                        break
                    }
                    Log.e(TAG, "Could not execute request", e)
                    failedRequests++
                    val millisSinceRequestStarted = SystemClock.elapsedRealtime() - requestStartTime
                    val sleepTime = TIMEOUT_SEC * 1000 - millisSinceRequestStarted
                    if (sleepTime > 0) {
                        Log.i(TAG, "Sleeping $sleepTime ms.")
                        sleep(sleepTime)
                    }
                }
            }
            Log.i(TAG, "Thread has finished")
        }

        companion object {
            private const val TIMEOUT_SEC = 60L
        }
    }

    override fun onDestroy() {
        thread.interrupt()
        Log.i(TAG, "Service onDestroy")
        super.onDestroy()
    }

    companion object {
        const val TAG = "BackgroundService"
    }
}