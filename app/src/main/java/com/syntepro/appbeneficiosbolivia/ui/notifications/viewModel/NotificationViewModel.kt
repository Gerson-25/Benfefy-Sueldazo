package com.syntepro.appbeneficiosbolivia.ui.notifications.viewModel

import androidx.lifecycle.MutableLiveData
import com.merckers.core.platform.BaseViewModel
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationRequest
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.NotificationResponse
import com.syntepro.appbeneficiosbolivia.ui.notifications.model.ReadNotificationRequest
import com.syntepro.appbeneficiosbolivia.ui.notifications.usecase.DoNotification
import com.syntepro.appbeneficiosbolivia.ui.notifications.usecase.DoReadNotification
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationViewModel
@Inject constructor(
        private val doNotification: DoNotification,
        private val doReadNotification: DoReadNotification
): BaseViewModel() {

    val notification: MutableLiveData<BaseResponse<List<NotificationResponse>>> = MutableLiveData()
    val readNotification: MutableLiveData<BaseResponse<Boolean>> = MutableLiveData()

    fun getNotification(request: NotificationRequest) =
            doNotification(DoNotification.Params(request)) {
                it.fold(::handleFailure, ::handleNotification)
            }

    fun readNotification(request: ReadNotificationRequest) =
            doReadNotification(DoReadNotification.Params(request)) {
                it.fold(::handleFailure, ::handleReadNotification)
            }

    private fun handleNotification(response: BaseResponse<List<NotificationResponse>>) {
        this.notification.value = response
    }

    private fun handleReadNotification(response: BaseResponse<Boolean>) {
        this.readNotification.value = response
    }

}