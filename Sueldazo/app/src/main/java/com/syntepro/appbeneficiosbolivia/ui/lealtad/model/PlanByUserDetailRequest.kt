package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class PlanByUserDetailRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val idPlanUser: String
)