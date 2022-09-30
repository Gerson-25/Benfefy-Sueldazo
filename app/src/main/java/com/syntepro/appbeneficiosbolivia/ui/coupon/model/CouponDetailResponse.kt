package com.syntepro.appbeneficiosbolivia.ui.coupon.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class CouponDetailResponse(
        val idCoupon: String,
        val cantExchage: Int,
        val cantExchangeUserLimit: Int,
        val description: String,
        val initialDate: Date,
        val finalDate: Date,
        val idCouponCategory: String,
        val idCommerce: String,
        val urlCommerceImage: String,
        val urlCouponImage: String,
        val commerceName: String,
        val realPrice: Double,
        val discountPrice: Double,
        val subtitle: String,
        val title: String,
        val conditions: String,
        val country: String,
        val whatsapp: String?,
        val facebook: String?,
        val instagram: String,
        val firebaseCodeType: Int,
        val favorite: Boolean,
        val generatedCoupons: Int,
        val qrCode: String
) {
    companion object {
        const val TRADITIONAL = 1
        const val BENEFIT = 1
        const val DISCOUNT_PERCENTAGE = 2
        const val FIXED_AMOUNT = 3
        const val SEALS = 5
        const val MILES = 6
    }
}