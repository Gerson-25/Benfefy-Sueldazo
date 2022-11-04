package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep
import com.syntepro.sueldazo.entity.service.UserOrder
import com.syntepro.sueldazo.ui.sudamericana.model.SurveyRequest

@Keep
class PaymentOrderRequest {
    var country: String? = null
    var language: Int = 0
    var idElement: String? = null
    var quantity: Int = 0
    var amount: Double = 0.0
    var elementType: Int = 1
    var userInfo: UserOrder? = UserOrder()
    var questionnaire: SurveyRequest? = null
    var presentDetail: UserArticleGift? = null
    var deliveryDetail: DeliveryDetail? = null
}