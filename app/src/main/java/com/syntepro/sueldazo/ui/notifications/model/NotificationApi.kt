package com.syntepro.sueldazo.ui.notifications.model

import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.service.ApiConfig.CONTENT_TYPE_JSON
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    companion object {
        private const val NOTIFICATION = "Notification/GetNotificationList"
        private const val READ_NOTIFICATION = "Notification/ReadNotification"
        private const val NOTIFICATION_COUNTER = "Notification/NotificationCount"
    }

    @Headers(CONTENT_TYPE_JSON)
    @POST(NOTIFICATION)
    fun getNotification(
            @Body body: NotificationRequest
    ): Call<BaseResponse<List<NotificationResponse>>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(READ_NOTIFICATION)
    fun readNotification(
            @Body body: ReadNotificationRequest
    ): Call<BaseResponse<Boolean>>

    @Headers(CONTENT_TYPE_JSON)
    @POST(NOTIFICATION_COUNTER)
    fun notificationCounter(
            @Body body: NotificationCountRequest
    ): Call<BaseResponse<NotificationCountResponse>>

}