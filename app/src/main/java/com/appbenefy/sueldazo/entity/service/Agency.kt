package com.appbenefy.sueldazo.entity.service

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Agency() : Serializable, Parcelable {
    var idProductAgency: String? = null
    var idProduct: String? = null
    var idAgency: String? = null
    var idCity: String? = null
    var idCommerce: String? = null
    var nameProduct: String? = null
    var addressProduct: String? = null
    var longitude: Double = 0.0
    var latitude: Double = 0.0
    var phone: String? = null

    constructor(parcel: Parcel) : this() {
        idProductAgency = parcel.readString()
        idProduct = parcel.readString()
        idAgency = parcel.readString()
        idCity = parcel.readString()
        idCommerce = parcel.readString()
        nameProduct = parcel.readString()
        addressProduct = parcel.readString()
        longitude = parcel.readDouble()
        latitude = parcel.readDouble()
        phone = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idProductAgency)
        parcel.writeString(idProduct)
        parcel.writeString(idAgency)
        parcel.writeString(idCity)
        parcel.writeString(idCommerce)
        parcel.writeString(nameProduct)
        parcel.writeString(addressProduct)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Agency> {
        override fun createFromParcel(parcel: Parcel): Agency {
            return Agency(parcel)
        }

        override fun newArray(size: Int): Array<Agency?> {
            return arrayOfNulls(size)
        }
    }
}