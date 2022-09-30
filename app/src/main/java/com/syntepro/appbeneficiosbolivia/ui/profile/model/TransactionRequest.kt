package com.syntepro.appbeneficiosbolivia.ui.profile.model

import androidx.annotation.Keep

@Keep
data class TransactionRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val idUser: String
)