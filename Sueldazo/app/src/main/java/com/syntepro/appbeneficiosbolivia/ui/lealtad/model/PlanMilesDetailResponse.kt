package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class PlanMilesDetailResponse(
        val idGoal: String?,
        val miles: Int,
        val goalDate: Date = Date(),
        val milesGoal: Int,
        val goalAdvance: Int,
        val idPlan: String,
        val plaName: String,
        val commerceName: String,
        val comerceImage: String,
        val commerceCategory: String,
        val couponList: List<MilesCoupon>
)