package com.appbenefy.sueldazo.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class FeaturedCouponResponse(
        val idCoupon: String,
        val title: String,
        val subtitle: String,
        val discountPrice: Double,
        val realPrice: Double,
        val couponImage: String,
        val commerceImage: String,
        val commerceName: String,
        val idCommerce: String,
        val couponType: Int,
        val favorite: Boolean,
        val vip: Boolean,
        val descriptionCampaing: String
)