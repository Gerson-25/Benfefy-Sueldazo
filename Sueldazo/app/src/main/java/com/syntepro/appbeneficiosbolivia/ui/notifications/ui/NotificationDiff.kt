package com.syntepro.appbeneficiosbolivia.ui.notifications.ui

import androidx.recyclerview.widget.DiffUtil
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationResponse

class NotificationDiff : DiffUtil.ItemCallback<NotificationResponse>() {
    override fun areItemsTheSame(oldItem: NotificationResponse, newItem: NotificationResponse): Boolean {
        return oldItem.idNotificationPush == newItem.idNotificationPush
    }

    override fun areContentsTheSame(oldItem: NotificationResponse, newItem: NotificationResponse): Boolean {
        return oldItem == newItem
    }

}