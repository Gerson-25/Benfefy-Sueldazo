package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class CommerceDetailRequest(
        val country: String,
        val language: Int,
        val idCommerce: String
)