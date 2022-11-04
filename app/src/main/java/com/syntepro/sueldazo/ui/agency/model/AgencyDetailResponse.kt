package com.syntepro.sueldazo.ui.agency.model

import androidx.annotation.Keep

@Keep
data class AgencyDetailResponse(
        val idAgency: String,
        val agencName: String,
        val address: String,
        val phoneNumber: String,
        val latitude: Double,
        val longitude: Double,
        val dayNumber: Int,
        val openingTime: String,
        val closingTime: String,
        val commerceName: String,
        val urlImageCommerce: String
)