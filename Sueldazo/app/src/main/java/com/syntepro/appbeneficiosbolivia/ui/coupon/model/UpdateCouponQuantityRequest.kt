package com.syntepro.appbeneficiosbolivia.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class UpdateCouponQuantityRequest(
        val country: String,
        val language: Int,
        val code: String,
        val quantity: Int
)