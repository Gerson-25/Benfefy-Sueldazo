package com.syntepro.appbeneficiosbolivia.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class BestDiscountRequest(
        val country: String,
        val language: Int,
        val idUser: String,
        val sortType: Int,
        val longitude: Double?,
        val latitude: Double?
) {
        companion object {
                const val SORT_TYPE_DISCOUNT = 1
                const val SORT_TYPE_LOCATION = 2
        }
}