package com.syntepro.appbeneficiosbolivia.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class FeaturedCouponRequest(
        val country: String,
        val language: Int,
        val idUser: String
)