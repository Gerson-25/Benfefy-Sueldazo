package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class PlanDetailResponse (
        val idPlan: String,
        val name: String,
        val termsAndConditions: String,
        val activeUsers: Int,
        val startDate: Date,
        val endDate: Date
)