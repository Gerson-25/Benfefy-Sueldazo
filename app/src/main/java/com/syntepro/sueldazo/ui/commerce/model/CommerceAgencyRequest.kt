package com.syntepro.sueldazo.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class CommerceAgencyRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val idCommerce: String,
        val longitude: Double,
        val latitude: Double
)