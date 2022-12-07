package com.appbenefy.sueldazo.ui.agency.model

import androidx.annotation.Keep

@Keep
data class AgencyDetailRequest(
        val country: String,
        val language: Int,
        val idAgency: String
)