package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DeliveryDetail(
        val direction: String,
        val latitude: Double,
        val longitude: Double,
        val idProvince: String,
        val contactName: String,
        val contactPhone: String,
        val contactEmail: String,
        val idDispatchPoint: String,
        val deliveryDate: String,
        val startTime: String,
        val endTime: String,
        val deliveryPaid: Boolean,
        val deliveryAmount: Double,
        val itemsPrice: Double
): Serializable