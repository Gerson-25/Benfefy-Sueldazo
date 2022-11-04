package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class PurchasedProductsRequest(
        var country: String,
        var language: Int,
        var recordsNumber: Int,
        var pageNumber: Long,
        var sortType: Long, // ASC or DESC
        var idUser: String
)