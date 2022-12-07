package com.appbenefy.sueldazo.ui.coupon.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class CouponListResponse(
        val idCoupon: String,
        val title: String,
        val subtitle: String,
        val discountPrice: Double,
        val realPrice: Double,
        val initDate: Date,
        val finalDate: Date,
        val exchangeLimit: Int,
        val exchangeUserLimit: Int,
        val description: String,
        val conditions: String,
        val firebaseCodeType: Int,
        val urlImageCoupon: String,
        val exchangeCount: Int,
        val valPercetage: Double,
        val commerceName: String,
        val urlImageCommerce: String,
        val favorite: Boolean,
        val vip: Boolean,
        val available: Boolean
)