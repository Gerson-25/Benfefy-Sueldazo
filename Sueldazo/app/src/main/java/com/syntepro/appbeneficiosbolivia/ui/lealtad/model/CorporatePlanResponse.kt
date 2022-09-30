package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class CorporatePlanResponse(
        val idPlan: String,
        val plaName: String,
        val commerceName: String,
        val comerceImage: String,
        val commerceCategory: String,
        val couponList: List<String>
)