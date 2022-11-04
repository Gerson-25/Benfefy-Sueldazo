package com.syntepro.sueldazo.ui.agency.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AgencyResponse(
        val idAgency: String?,
        val sucursalID: String?,
        val idCommerce: String,
        val commerceID: String,
        val agencyName: String?,
        val sucursalName: String?,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val distance: Double,
        @SerializedName("couponsCount")
        val couponCount: Int,
        val commerceName: String,
        val urlCommerceImage: String?,
        val urlImage: String?,
        val city: String,
        val idCity: String?,
        val state: String,
        val idState: String,
        val phone: String
)
