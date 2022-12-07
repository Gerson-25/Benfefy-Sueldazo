package com.appbenefy.sueldazo.ui.profile.model

import androidx.annotation.Keep

@Keep
data class UserStatsResponse(
        val savings: Double,
        val loses: Double,
        val yearStats: List<YearStats>
)