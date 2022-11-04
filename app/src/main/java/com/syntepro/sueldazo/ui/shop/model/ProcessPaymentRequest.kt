package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep

@Keep
data class ProcessPaymentRequest(
        val country: String,
        val language: Int,
        val cardNumber: String,
        val month: Int,
        val year: Int,
        val nameClient: String,
        val cvvCard: String,
        val idOrderPayment: String,
        val idUser: String
)