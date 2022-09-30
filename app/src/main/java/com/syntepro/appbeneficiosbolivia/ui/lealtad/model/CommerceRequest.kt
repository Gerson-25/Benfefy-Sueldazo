package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class CommerceRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val idCategory: String,
        val loyaltyPlan: Boolean,
        val idUser: String,
        val longitude: Double?,
        val latitude: Double?
)
