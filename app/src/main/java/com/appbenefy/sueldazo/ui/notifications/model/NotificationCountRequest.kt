package com.appbenefy.sueldazo.ui.notifications.model

import androidx.annotation.Keep

@Keep
data class NotificationCountRequest(
        val country: String,
        val language: Int,
        val idUser: String
)