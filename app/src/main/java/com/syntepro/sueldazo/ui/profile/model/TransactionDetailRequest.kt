package com.syntepro.sueldazo.ui.profile.model

import androidx.annotation.Keep

@Keep
data class TransactionDetailRequest(
        val country: String,
        val language: Int,
        val idTransaction: String
)