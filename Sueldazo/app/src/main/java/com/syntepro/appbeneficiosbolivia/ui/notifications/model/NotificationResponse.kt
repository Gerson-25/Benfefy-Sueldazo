package com.syntepro.appbeneficiosbolivia.ui.notifications.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class NotificationResponse(
        val idNotificationPush: String,
        val idNotificationType: Int,
        val title: String,
        val subtitle: String,
        val payload: String,
        val read: Boolean,
        val dateRead: Date?,
        val idUserApp: String,
        val creationDate: Date
)