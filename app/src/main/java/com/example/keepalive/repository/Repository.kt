package com.example.keepalive.repository

interface Repository {
    suspend fun sendPlainPing(userId: Long, message: String): String

    suspend fun sendLongPollingPing(userId: Long, message: String, timeoutInSeconds: Long): String
}