package com.example.keepalive.repository

import com.example.keepalive.network.Api
import com.example.keepalive.utils.MessageConstructor

class RepositoryImpl : Repository {
    override suspend fun sendPlainPing(userId: Long, message: String): String {
        return Api.ApiImpl.sendPlainPing(userId, wrapMessage(message))
    }

    override suspend fun sendLongPollingPing(
        userId: Long,
        message: String,
        timeoutInSeconds: Long
    ): String {
        return Api.ApiImpl.sendLongPolling(userId, wrapMessage(message), timeoutInSeconds)
    }

    private fun wrapMessage(message: String): String {
        return MessageConstructor.createMessage(message)
    }
}