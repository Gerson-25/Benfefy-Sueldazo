package com.syntepro.sueldazo.ui.notifications.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.notifications.model.NotificationRepository
import com.syntepro.sueldazo.ui.notifications.model.NotificationRequest
import com.syntepro.sueldazo.ui.notifications.model.NotificationResponse
import javax.inject.Inject

class DoNotification
@Inject constructor(private val notificationRepository: NotificationRepository) : UseCase<BaseResponse<List<NotificationResponse>>, DoNotification.Params>() {

    override suspend fun run(params: Params) = notificationRepository.getNotifications(params.request)

    data class Params(val request: NotificationRequest)
}