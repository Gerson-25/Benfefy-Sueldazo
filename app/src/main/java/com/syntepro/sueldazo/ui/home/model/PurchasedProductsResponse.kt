package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class PurchasedProductsResponse (
        val idPurchasedProductIndex: Int,
        val productsAvailables: Int,
        val title: String,
        val subtitle: String,
        val percentPrice: Double,
        val regularPrice: Double,
        val idProductType: Int,
        val idProduct: String,
        val idComercio: String,
        val urlArt: String,
        val favorite: Boolean,
        val blocked: Boolean
) {
    companion object {
        const val PURCHASED_COUPON = 1
        const val PURCHASED_GIFT_CARD = 2
        const val WELCOME_COUPON = 3
        const val PROMOTIONAL_COUPON = 4
        const val CORPORATE_VOUCHER = 7
        const val ARTICLE_GIFT = 8
        const val GIFT_CARD_GIFT = 9
        const val INSURANCE_GIFT = 10
    }
}