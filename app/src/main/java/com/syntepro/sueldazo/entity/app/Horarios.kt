package com.syntepro.sueldazo.entity.app

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Horarios() : Serializable, Parcelable {
    var idSucursal: String? = null
    var diaSemana: Int = 0
    var txtDiaSemana: String? = null
    var horaApertura: String? = null
    var horaCierre: String? = null
    var cierraMediodia: Boolean = false
    var cerrado: Boolean = false

    constructor(parcel: Parcel) : this() {
        idSucursal = parcel.readString()
        diaSemana = parcel.readInt()
        txtDiaSemana = parcel.readString()
        horaApertura = parcel.readString()
        horaCierre = parcel.readString()
        cierraMediodia = parcel.readByte() != 0.toByte()
        cerrado = parcel.readByte() != 0.toByte()
    }

    override fun toString(): String {
        val horaA = horaApertura
        val partHora = horaA!!.split(":")
        val apertura = partHora[0] + ":" + partHora[1]

        val horaC = horaCierre
        val partHoraC = horaC!!.split(":")
        val cierre = partHoraC[0] + ":" + partHoraC[1]

        return "$txtDiaSemana $apertura - $cierre"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idSucursal)
        parcel.writeInt(diaSemana)
        parcel.writeString(txtDiaSemana)
        parcel.writeString(horaApertura)
        parcel.writeString(horaCierre)
        parcel.writeByte(if (cierraMediodia) 1 else 0)
        parcel.writeByte(if (cerrado) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Horarios> {
        override fun createFromParcel(parcel: Parcel): Horarios {
            return Horarios(parcel)
        }

        override fun newArray(size: Int): Array<Horarios?> {
            return arrayOfNulls(size)
        }
    }
}