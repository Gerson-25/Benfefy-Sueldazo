package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude
import syntepro.util.picker.ListablePicker

@Keep
data class PlanByCommerceResponse(
        val idLoyaltyPlan: String,
        val name: String,
        val description: String,
        val codeRequired: Boolean
): Parcelable, ListablePicker {
    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idLoyaltyPlan)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeByte(if (codeRequired) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlanByCommerceResponse> {
        override fun createFromParcel(parcel: Parcel): PlanByCommerceResponse {
            return PlanByCommerceResponse(parcel)
        }

        override fun newArray(size: Int): Array<PlanByCommerceResponse?> {
            return arrayOfNulls(size)
        }
    }

        @Exclude
        override fun getCodeValue(): String  { return "" }
        @Exclude
        override fun getTitleValue(): String  { return name }
        @Exclude
        override fun getDescValue(): String  { return description }

    override fun toString(): String {
        return name
    }
}