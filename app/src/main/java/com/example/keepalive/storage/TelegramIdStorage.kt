package com.example.keepalive.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TelegramIdStorage(private val context: Context) {
    private val userIdKey = longPreferencesKey("user_id")

    fun observeUserId(): Flow<Long> {
        return context.dataStore.data.map { it[userIdKey] ?: 0L }
    }

    suspend fun getUserId(): Long {
        return observeUserId().first()
    }

    suspend fun containsUserId(): Boolean {
        return getUserId() > 0L
    }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { it[userIdKey] = userId }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(userIdKey) }
    }

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "storage")
    }
}