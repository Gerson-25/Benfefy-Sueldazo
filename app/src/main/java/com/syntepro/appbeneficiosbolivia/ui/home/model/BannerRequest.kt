package com.syntepro.appbeneficiosbolivia.ui.home.model

import androidx.annotation.Keep

@Keep
data class BannerRequest(
    var country: String,
    var language: Int
)