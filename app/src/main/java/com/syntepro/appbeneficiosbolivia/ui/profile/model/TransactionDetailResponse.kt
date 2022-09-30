package com.syntepro.appbeneficiosbolivia.ui.profile.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class TransactionDetailResponse(
        val idTransaction: String,
        val idTransactionType: Int,
        val idUser: String,
        val idCommerce: String,
        val commerceName: String,
        val title: String,
        val description: String,
        val transactionDate: Date,
        val agencyName: String?,
        val cashier: String?,
        val userGivesName: String?,
        val userReceivesName: String?,
        val articleName: String?,
        val giftCode: String,
        val idReference: String?
)