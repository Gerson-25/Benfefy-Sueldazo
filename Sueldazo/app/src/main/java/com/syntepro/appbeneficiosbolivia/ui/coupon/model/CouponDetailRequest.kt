package com.syntepro.appbeneficiosbolivia.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class CouponDetailRequest(
        val country: String,
        val language: Int,
        val idCoupon: String,
        val idUser: String
)