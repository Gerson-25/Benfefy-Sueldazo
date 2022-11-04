package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class BannerRequest(
    var country: String,
    var language: Int,
    var filterType: Int
)