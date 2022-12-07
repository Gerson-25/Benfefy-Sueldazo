package com.appbenefy.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class TransactionData(
        val idTransactionType: Int,
        val name: String
)