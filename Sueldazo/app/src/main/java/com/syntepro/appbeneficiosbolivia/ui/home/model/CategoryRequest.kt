package com.syntepro.appbeneficiosbolivia.ui.home.model

import androidx.annotation.Keep

@Keep
data class CategoryRequest(
        val country: String,
        val language: Int,
        val filterType: Int?
)