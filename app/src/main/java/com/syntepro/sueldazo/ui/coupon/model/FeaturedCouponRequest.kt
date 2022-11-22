package com.syntepro.sueldazo.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class FeaturedCouponRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val idCategoryType: String
)