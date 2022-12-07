package com.appbenefy.sueldazo.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class CommerceDetailResponse(
        val idCommerce: String,
        val commerceName: String,
        val commerceImage: String,
        val commerceBranch: String,
        val commerceDescription: String,
        val commerceFacebook: String,
        val commerceInstagram: String,
        val commerceWhatsapp: String,
        val commercePhone: String,
        val commerceEmail: String,
        val commerceWebSite: String,
        val commerceOpeningTime: String,
        val commerceClosingTime: String
)