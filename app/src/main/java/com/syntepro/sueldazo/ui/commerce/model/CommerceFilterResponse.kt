package com.syntepro.sueldazo.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class CommerceFilterResponse(
        val idCommerce: String,
        val commerceName: String,
        val urlCommerce: String
)