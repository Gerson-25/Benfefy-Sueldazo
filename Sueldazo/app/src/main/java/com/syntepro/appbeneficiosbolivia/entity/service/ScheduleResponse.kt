package com.syntepro.appbeneficiosbolivia.entity.service

import androidx.annotation.Keep

@Keep
data class ScheduleResponse(
        val idDispatchPoint: String,
        val startTime: String,
        val endTime: String,
        val isOpen: Boolean
)