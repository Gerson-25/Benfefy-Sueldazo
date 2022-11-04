package com.syntepro.sueldazo.ui.benefy.model

import androidx.annotation.Keep

@Keep
data class PurchasedProductDetailRequest(
    val country: String,
    val language: Int,
    val idProductIndex: Int
)