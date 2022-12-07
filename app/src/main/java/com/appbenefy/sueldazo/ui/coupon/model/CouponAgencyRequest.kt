package com.appbenefy.sueldazo.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class CouponAgencyRequest(
        val country: String,
        val language: Int,
        val idCoupon: String,
        val latitude: Double,
        val longitude: Double,
        val justOne: Boolean
)