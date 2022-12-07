package com.appbenefy.sueldazo.ui.notifications.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.notifications.model.NotificationCountRequest
import com.appbenefy.sueldazo.ui.notifications.model.NotificationCountResponse
import com.appbenefy.sueldazo.ui.notifications.model.NotificationRepository
import javax.inject.Inject

class DoNotificationCount
@Inject constructor(private val notificationRepository: NotificationRepository) : UseCase<BaseResponse<NotificationCountResponse>, DoNotificationCount.Params>() {

    override suspend fun run(params: Params) = notificationRepository.fetchNotificationCounter(params.request)

    data class Params(val request: NotificationCountRequest)
}