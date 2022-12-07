package com.appbenefy.sueldazo.entity.service

import androidx.annotation.Keep

@Keep
data class ScheduleRequest(
        val country: String,
        val language: Int,
        val idDispatchPoint: String,
        val date: String
)