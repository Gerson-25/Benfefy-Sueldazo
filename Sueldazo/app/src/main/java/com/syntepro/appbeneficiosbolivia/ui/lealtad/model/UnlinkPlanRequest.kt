package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class UnlinkPlanRequest(
        var country: String,
        var language: Int,
        var idUserPlan: String,
        var username: String
)