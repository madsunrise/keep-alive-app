package com.example.keepalive.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.keepalive.repository.Repository
import com.example.keepalive.repository.RepositoryImpl

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
        return try {
            val text = "plain worker"
            repository.sendPlainPing(userId, text)
            Log.i(TAG, "Ping has finished")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to ping server", e)
            Result.retry()
        }
    }


    companion object {
        const val KEY_USER_ID = "KEY_USER_ID"
        const val TAG = "PlainWorker"
    }
}