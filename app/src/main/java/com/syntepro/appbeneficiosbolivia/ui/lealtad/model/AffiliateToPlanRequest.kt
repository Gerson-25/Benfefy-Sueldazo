package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class AffiliateToPlanRequest(
        val country: String,
        val language: Int,
        val idPlan: String,
        val idUser: String,
        val nameUser: String,
        val planCode: String?
)