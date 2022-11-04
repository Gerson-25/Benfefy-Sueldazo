package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep

@Keep
data class DistanceAmountDeliveryResponse(
        val price: Double,
        val distance: Double
)