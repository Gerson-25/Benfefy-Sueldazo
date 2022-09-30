package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class PlanDetailRequest(
        val country: String,
        val language: Int,
        val idPlan: String
)