package com.syntepro.appbeneficiosbolivia.ui.shop.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.entity.service.UserOrder
import com.syntepro.appbeneficiosbolivia.ui.sudamericana.model.SurveyRequest

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