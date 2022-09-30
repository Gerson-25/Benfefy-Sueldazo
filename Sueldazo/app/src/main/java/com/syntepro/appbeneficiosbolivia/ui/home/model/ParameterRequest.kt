package com.syntepro.appbeneficiosbolivia.ui.home.model

import androidx.annotation.Keep

@Keep
data class ParameterRequest(
        val country: String,
        val language: Int
)