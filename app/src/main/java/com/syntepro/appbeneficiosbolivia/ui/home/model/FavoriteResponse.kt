package com.syntepro.appbeneficiosbolivia.ui.home.model

import androidx.annotation.Keep

@Keep
data class FavoriteResponse(
        val idFavorite: String,
        val idCoupon: String,
        val idCouponType: Int,
        val name: String?,
        val title: String,
        val subTitle: String,
        val percentPrice: Double,
        val regularPrice: Double,
        val urlCoupon: String,
        val urlCommerce: String,
        val fbCodeType: Int,
        val available: Boolean
)