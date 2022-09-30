package com.syntepro.appbeneficiosbolivia.ui.shop.model

import androidx.annotation.Keep

@Keep
data class PaymentOrderResponse(
        val urlPayment: String,
        val payType: Int,
        val idOrder: String?
)