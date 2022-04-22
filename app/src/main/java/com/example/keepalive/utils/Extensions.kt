package com.example.keepalive.utils

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

object Extensions {
    fun Fragment.toast(msg: String?, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(activity, msg, length).show()
    }
}