package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class LoyaltyPlanByUser(
        val idLoyaltyPlan: String,
        val name: String,
        val commerceName: String,
        val commerceImage: String
)