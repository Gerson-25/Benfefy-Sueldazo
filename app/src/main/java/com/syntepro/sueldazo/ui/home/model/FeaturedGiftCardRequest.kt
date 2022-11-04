package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class FeaturedGiftCardRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val sortType: Int,
        val idCity: String?,
        val idCategory: String?
)