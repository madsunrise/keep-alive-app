package com.example.keepalive.utils

import android.os.Build
import com.example.keepalive.BuildConfig

object MessageConstructor {
    fun createMessage(text: String): String {
        return "${Build.MODEL}: $text"
    }
}