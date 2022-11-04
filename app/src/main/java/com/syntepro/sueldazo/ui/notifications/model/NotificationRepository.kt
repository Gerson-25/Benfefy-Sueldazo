package com.syntepro.sueldazo.ui.notifications.model

import com.merckers.core.exception.Failure
import com.merckers.core.functional.Either
import com.merckers.core.platform.NetworkHandler
import com.syntepro.sueldazo.base.BaseRepository
import com.syntepro.sueldazo.core.entities.BaseResponse
import javax.inject.Inject

interface NotificationRepository {

    fun getNotifications(request: NotificationRequest): Either<Failure, BaseResponse<List<NotificationResponse>>>
    fun readNotifications(request: ReadNotificationRequest): Either<Failure, BaseResponse<Boolean>>
    fun fetchNotificationCounter(request: NotificationCountRequest): Either<Failure, BaseResponse<NotificationCountResponse>>

    class Network
    @Inject constructor(
            private val networkHandler: NetworkHandler,
            private val service: NotificationService
    ) : NotificationRepository, BaseRepository() {

        override fun getNotifications(request: NotificationRequest): Either<Failure, BaseResponse<List<NotificationResponse>>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getNotification(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun readNotifications(request: ReadNotificationRequest): Either<Failure, BaseResponse<Boolean>> {
            return when (networkHandler.isConnected) {
                true -> request(service.readNotification(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun fetchNotificationCounter(request: NotificationCountRequest): Either<Failure, BaseResponse<NotificationCountResponse>> {
            return when (networkHandler.isConnected) {
                true -> request(service.notificationCounter(request), { it }, BaseResponse.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

    }

}