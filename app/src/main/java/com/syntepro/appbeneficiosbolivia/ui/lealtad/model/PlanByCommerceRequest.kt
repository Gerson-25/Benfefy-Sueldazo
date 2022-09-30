package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class PlanByCommerceRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val idCommerce: String,
        val idUser: String)