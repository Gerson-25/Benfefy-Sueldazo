package com.syntepro.appbeneficiosbolivia.ui.notifications.model

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService
@Inject constructor(retrofit: Retrofit) : NotificationApi {
    private val notificationApiApi by lazy { retrofit.create(NotificationApi::class.java) }

    override fun getNotification(body: NotificationRequest): Call<BaseResponse<List<NotificationResponse>>> = notificationApiApi.getNotification(body)
    override fun readNotification(body: ReadNotificationRequest): Call<BaseResponse<Boolean>> = notificationApiApi.readNotification(body)
    override fun notificationCounter(body: NotificationCountRequest): Call<BaseResponse<NotificationCountResponse>> = notificationApiApi.notificationCounter(body)

}