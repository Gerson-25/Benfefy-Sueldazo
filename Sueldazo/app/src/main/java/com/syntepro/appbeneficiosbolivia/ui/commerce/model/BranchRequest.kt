package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class BranchRequest(
        var country: String,
        var language: Int
)