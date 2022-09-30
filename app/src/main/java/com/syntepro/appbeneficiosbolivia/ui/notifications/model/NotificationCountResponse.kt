package com.syntepro.appbeneficiosbolivia.ui.notifications.model

import androidx.annotation.Keep

@Keep
data class NotificationCountResponse(
        val idUser: String,
        val count: Int
)