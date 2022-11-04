package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep

@Keep
data class CountriesRequest(
        val country: String,
        val language: Int,
        val filterType: Int
)