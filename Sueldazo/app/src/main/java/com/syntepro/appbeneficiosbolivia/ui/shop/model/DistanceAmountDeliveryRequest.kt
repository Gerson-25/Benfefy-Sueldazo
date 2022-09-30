package com.syntepro.appbeneficiosbolivia.ui.shop.model

import androidx.annotation.Keep

@Keep
data class DistanceAmountDeliveryRequest(
     val country: String,
     val language: Int,
     val idDispatchPoint: String,
     val productPrice: Double,
     val userLat: Double,
     val userLon: Double
)