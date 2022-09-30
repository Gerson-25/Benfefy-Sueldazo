package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class Card(
        val idCard: String,
        val obtainedStamps: Int,
        val requiredStamps: Int,
        val isRedeem: Boolean,
        val expired: Boolean,
        val stamps: List<Stamp>
)