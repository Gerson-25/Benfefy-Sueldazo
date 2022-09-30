package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class CardDetailRequest (
        val country: String,
        val language: Int,
        val idCard: String
)