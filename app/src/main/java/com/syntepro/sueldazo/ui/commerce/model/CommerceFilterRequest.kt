package com.syntepro.sueldazo.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class CommerceFilterRequest(
        val country: String,
        val language: Int,
        val filterText: String
)