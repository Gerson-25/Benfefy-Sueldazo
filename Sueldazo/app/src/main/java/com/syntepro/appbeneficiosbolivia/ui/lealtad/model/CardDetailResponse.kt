package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class CardDetailResponse (
        val idCard: String,
        val state: String,
        val obtainedStamp: Int,
        val requiredStamp: Int,
        val stardDate: Date,
        val endDate: Date,
        val dateCompleted: Date?,
        val exchangeDate: Date?,
        val dateLastStamp: Date?
)