package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class FavoriteRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val sortType: Int,
        val idUser: String
)