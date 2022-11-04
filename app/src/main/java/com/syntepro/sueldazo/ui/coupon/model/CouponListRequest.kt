package com.syntepro.sueldazo.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class CouponListRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val idUser: String,
        val idCategory: String?,
        val idCommerce: String?
)