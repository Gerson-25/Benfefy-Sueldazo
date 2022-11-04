package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class BannerResponse (
    val idBanner: String,
    val idBannerTypeAction: Int,
    val campaingName: String,
    val initDate: Date,
    val endDate: Date,
    val active: Boolean,
    val urlImage: String,
    val urlSite: String,
    val idCouponType: String,
)