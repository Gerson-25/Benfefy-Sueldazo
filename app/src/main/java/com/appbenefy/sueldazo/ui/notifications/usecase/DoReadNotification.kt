package com.appbenefy.sueldazo.ui.notifications.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.notifications.model.NotificationRepository
import com.appbenefy.sueldazo.ui.notifications.model.ReadNotificationRequest
import javax.inject.Inject

class DoReadNotification
@Inject constructor(private val notificationRepository: NotificationRepository) : UseCase<BaseResponse<Boolean>, DoReadNotification.Params>() {

    override suspend fun run(params: Params) = notificationRepository.readNotifications(params.request)

    data class Params(val request: ReadNotificationRequest)
}