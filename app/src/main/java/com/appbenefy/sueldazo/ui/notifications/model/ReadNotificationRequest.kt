package com.appbenefy.sueldazo.ui.notifications.model

import androidx.annotation.Keep

@Keep
data class ReadNotificationRequest(
        val country: String,
        val language: Int,
        val idNotificationPush: String
)