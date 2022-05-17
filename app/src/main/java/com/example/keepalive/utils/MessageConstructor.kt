package com.example.keepalive.utils

import android.os.Build

object MessageConstructor {
    fun createMessage(text: String): String {
        return "${Build.MANUFACTURER} ${Build.MODEL} (${Build.VERSION.RELEASE}): $text"
    }
}