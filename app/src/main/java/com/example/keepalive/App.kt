package com.example.keepalive

import android.app.Application
import com.example.keepalive.network.Api

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Api.ApiImpl.init()
    }
}