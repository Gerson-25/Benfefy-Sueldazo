package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep

@Keep
data class GiftDetailRequest(
        val country: String,
        val language: Int,
        val idOrderPresent: String
)