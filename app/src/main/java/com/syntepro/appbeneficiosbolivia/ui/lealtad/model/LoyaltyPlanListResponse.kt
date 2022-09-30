package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class LoyaltyPlanListResponse(
        val cardPlans: List<LoyaltyPlanByUser>?,
        val corporatePlans: List<LoyaltyPlanByUser>,
        val milesPlan: List<LoyaltyPlanByUser>
)