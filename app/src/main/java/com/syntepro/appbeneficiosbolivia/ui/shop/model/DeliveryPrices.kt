package com.syntepro.appbeneficiosbolivia.ui.shop.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DeliveryPrices(
        val idCommerceDeliveryPrice: String,
        val idCommerce: String,
        val startStripe: Double,
        val endStripe: Double,
        val charge: Double
): Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble())

    override fun describeContents(): Int { return 0 }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idCommerceDeliveryPrice)
        parcel.writeString(idCommerce)
        parcel.writeDouble(startStripe)
        parcel.writeDouble(endStripe)
        parcel.writeDouble(charge)
    }

    companion object CREATOR : Parcelable.Creator<DeliveryPrices> {
        override fun createFromParcel(parcel: Parcel): DeliveryPrices {
            return DeliveryPrices(parcel)
        }

        override fun newArray(size: Int): Array<DeliveryPrices?> {
            return arrayOfNulls(size)
        }
    }

}