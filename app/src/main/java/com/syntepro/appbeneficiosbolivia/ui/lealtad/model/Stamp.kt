package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep
import java.io.Serializable
import java.util.*

@Keep
data class Stamp (
        val idStampLoyaltyPlan: String,
        val idCardLoyaltyPlan: String,
        val payDesk: String,
        val cashier: String,
        val description: String,
        val date: Date,
        val agency: String
): Serializable