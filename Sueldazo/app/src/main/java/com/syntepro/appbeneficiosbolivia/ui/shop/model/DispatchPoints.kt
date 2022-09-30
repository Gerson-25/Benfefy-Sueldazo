package com.syntepro.appbeneficiosbolivia.ui.shop.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class DispatchPoints(
        val idCommerceDispathPoint: String,
        val idCommerce: String,
        val name: String,
        val longitude: Double,
        val latitude: Double,
        val phone: String
): Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString() ?: "") {
    }

    override fun describeContents(): Int { return 0 }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idCommerceDispathPoint)
        parcel.writeString(idCommerce)
        parcel.writeString(name)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeString(phone)
    }

    companion object CREATOR : Parcelable.Creator<DispatchPoints> {
        override fun createFromParcel(parcel: Parcel): DispatchPoints {
            return DispatchPoints(parcel)
        }

        override fun newArray(size: Int): Array<DispatchPoints?> {
            return arrayOfNulls(size)
        }
    }
}