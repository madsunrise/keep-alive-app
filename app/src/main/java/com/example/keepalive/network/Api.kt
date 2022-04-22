package com.example.keepalive.network

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface Api {
    @POST("ping")
    suspend fun pingPlain(@Body body: PlainPingRequest): Response<PingResponse>

    @POST("ping_long_polling")
    suspend fun pingLongPolling(@Body body: LongPollingPingRequest): Response<PingResponse>

    object ApiImpl {
        private lateinit var retrofit: Retrofit
        private lateinit var api: Api
        private var isInitialized = false

        @Synchronized
        fun init() {
            if (isInitialized) {
                throw IllegalStateException("Api is already initialized")
            }
            retrofit = Retrofit.Builder()
                .baseUrl("http://95.217.190.229:8080/keep-alive-bot/")
                .client(createOkHttpClient())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            api = retrofit.create(Api::class.java)
            isInitialized = true
        }

        suspend fun sendPlainPing(userId: Long, text: String): String {
            val body = PlainPingRequest(userId, text)
            return api.pingPlain(body).body()!!.status
        }

        suspend fun sendLongPolling(userId: Long, text: String, timeoutInSeconds: Long): String {
            val body = LongPollingPingRequest(userId, text, timeoutInSeconds)
            return api.pingLongPolling(body).body()!!.status
        }

        private fun createOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build()
        }
    }
}