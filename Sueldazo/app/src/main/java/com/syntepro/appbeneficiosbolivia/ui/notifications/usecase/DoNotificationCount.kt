package com.syntepro.appbeneficiosbolivia.ui.notifications.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationCountRequest
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationCountResponse
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationRepository
import javax.inject.Inject

class DoNotificationCount
@Inject constructor(private val notificationRepository: NotificationRepository) : UseCase<BaseResponse<NotificationCountResponse>, DoNotificationCount.Params>() {

    override suspend fun run(params: Params) = notificationRepository.fetchNotificationCounter(params.request)

    data class Params(val request: NotificationCountRequest)
}