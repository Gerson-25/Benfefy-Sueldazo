package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep

@Keep
data class GiftDetailResponse(
     val idOrderPresent: String,
     val urlImage: String,
     val title: String,
     val subTitle: String,
     val amount: Double,
     val idProductType: Int
)