package com.appbenefy.sueldazo.ui.notifications.ui

import androidx.recyclerview.widget.DiffUtil
import com.appbenefy.sueldazo.ui.notifications.model.NotificationResponse

class NotificationDiff : DiffUtil.ItemCallback<NotificationResponse>() {
    override fun areItemsTheSame(oldItem: NotificationResponse, newItem: NotificationResponse): Boolean {
        return oldItem.idNotificationPush == newItem.idNotificationPush
    }

    override fun areContentsTheSame(oldItem: NotificationResponse, newItem: NotificationResponse): Boolean {
        return oldItem == newItem
    }

}