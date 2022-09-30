package com.syntepro.appbeneficiosbolivia.ui.profile.model

import androidx.annotation.Keep

@Keep
data class UserStatsRequest(
        val country: String,
        val language: Int,
        val idUser: String,
        val month: Int,
        val year: Int
)