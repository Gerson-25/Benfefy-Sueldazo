package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class CommerceFilterRequest(
        val country: String,
        val language: Int,
        val filterText: String
)