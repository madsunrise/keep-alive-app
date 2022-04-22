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

/**
 * Executes one request and finishes
 */
class PlainWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val repository: Repository = RepositoryImpl()

    override suspend fun doWork(): Result {
        Log.i(TAG, "Plain worker has started")
        val userId = inputData.getLong(KEY_USER_ID, 0)
        if (userId == 0L) {
            throw IllegalArgumentException("Provide user ID extra")
        }
        try {
            val text = "plain worker"
            repository.sendPlainPing(userId, text)
            Log.i(TAG, "Ping has finished")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to ping server", e)
        }
        return Result.success()
    }


    companion object {
        const val KEY_USER_ID = "KEY_USER_ID"
        const val TAG = "PlainWorker"
    }
}