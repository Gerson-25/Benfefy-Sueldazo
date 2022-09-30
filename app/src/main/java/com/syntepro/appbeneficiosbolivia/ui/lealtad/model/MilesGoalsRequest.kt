package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class MilesGoalsRequest(
        val country: String,
        val language: Int,
        val idGoal: String?,
        val idUser: String,
        val idLoyaltyPlan: String,
        val goal: Int,
        val dateGoal: String
)