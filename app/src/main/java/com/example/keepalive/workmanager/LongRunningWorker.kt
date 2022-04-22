package com.example.keepalive.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.keepalive.repository.Repository
import com.example.keepalive.repository.RepositoryImpl
import kotlinx.coroutines.delay
import kotlin.properties.Delegates.notNull
import kotlin.random.Random

class LongRunningWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val notificationManager = context.getSystemService<NotificationManager>()!!
    private val repository: Repository = RepositoryImpl()

    private var totalRequests = 0
    private var failedRequests = 0
    private var workStartedTime: Long by notNull()

    private val notificationId = Random.Default.nextInt()

    override suspend fun doWork(): Result {
        Log.i(TAG, "Long-running worker has started")
        workStartedTime = SystemClock.elapsedRealtime()
        val userId = inputData.getLong(KEY_USER_ID, 0)
        if (userId == 0L) {
            throw IllegalArgumentException("Provide user ID extra")
        }
        val timeoutInSeconds = inputData.getLong(KEY_LONG_POLLING_TIMEOUT, 0L)
        if (timeoutInSeconds == 0L) {
            throw IllegalArgumentException("Provide timeout extra")
        }
        setForeground(createForegroundInfo())
        startPolling(userId, timeoutInSeconds)
        return Result.success()
    }

    private suspend fun startPolling(userId: Long, timeoutInSeconds: Long) {
        while (!isStopped) {
            val text = "long running worker, request #${++totalRequests}"
            val requestStartTime = SystemClock.elapsedRealtime()
            try {
                repository.sendLongPollingPing(userId, text, timeoutInSeconds)
                updateNotification()
            } catch (e: Exception) {
                if (isStopped) {
                    break
                }
                Log.e(TAG, "Could not execute request", e)
                failedRequests++
                updateNotification()
                val millisSinceRequestStarted = SystemClock.elapsedRealtime() - requestStartTime
                val sleepTime = timeoutInSeconds * 1000 - millisSinceRequestStarted
                Log.i(TAG, "Sleeping $sleepTime ms.")
                delay(sleepTime)
            }
        }
    }

    private fun updateNotification() {
        val notification = createNotification()
        notificationManager.notify(notificationId, notification)
    }

    private fun createForegroundInfo(): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        val notification = createNotification()
        return ForegroundInfo(notificationId, notification)
    }

    private fun createNotification(): Notification {
        val cancelIntent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Long running worker")
            .setTicker("Long running worker")
            .setContentText(createContentText())
            .setStyle(NotificationCompat.BigTextStyle())
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .addAction(android.R.drawable.ic_delete, "Cancel", cancelIntent)
            .build()
    }

    private fun createContentText(): String {
        return "Total requests: $totalRequests, failed requests: $failedRequests\n" +
            "Running for ${getCurrentWorkDuration()}"
    }

    private fun getCurrentWorkDuration(): String {
        val durationInMillis = SystemClock.elapsedRealtime() - workStartedTime
        val durationInMinutes = durationInMillis / 1000 / 60
        if (durationInMinutes >= 60) {
            val hours = durationInMinutes / 60
            return "${hours}h ${durationInMinutes % 60}m"
        } else {
            return "$durationInMinutes minutes"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = "Notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.enableVibration(true)
        channel.enableLights(true)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val KEY_USER_ID = "KEY_USER_ID"
        const val KEY_LONG_POLLING_TIMEOUT = "KEY_LONG_POLLING_TIMEOUT"
        private const val CHANNEL_ID = "channel_id"
        private const val TAG = "LongRunningWorker"
    }
}