package com.syntepro.sueldazo.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class SaveCouponRatingRequest(
        val country: String,
        val language: Int,
        val idProduct: String,
        val question1: Int,
        val question2: Int,
        val idUser: String,
        val qrCode: String,
        val idProductType: Int
) {
    companion object {
        const val ARTICLE_TYPE = 1
        const val GIFT_TYPE = 2
        const val WELCOME_TYPE = 3
        const val HUAWEI_TYPE = 4
        const val PERSONAL_ACCIDENT_INSURANCE_TYPE = 5
        const val PET_INSURANCE_TYPE = 6
    }
}