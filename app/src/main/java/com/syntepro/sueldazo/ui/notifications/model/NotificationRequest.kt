package com.syntepro.sueldazo.ui.notifications.model

import androidx.annotation.Keep

@Keep
data class NotificationRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val idUser: String
)