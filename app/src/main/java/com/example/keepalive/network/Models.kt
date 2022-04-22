package com.example.keepalive.network

import com.squareup.moshi.Json

class PlainPingRequest(
    @field:Json(name = "user_id") val userId: Long,
    @field:Json(name = "text") val text: String
)

class LongPollingPingRequest(
    @field:Json(name = "user_id") val userId: Long,
    @field:Json(name = "text") val text: String,
    @field:Json(name = "timeout_in_seconds") val timeoutInSeconds: Long
)

class PingResponse(
    @field:Json(name = "status") val status: String
)

