package com.appbenefy.sueldazo.entity.firebase

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import syntepro.util.picker.ListablePicker
import java.util.*

class SeguirComercio: ListablePicker {
    @DocumentId
    var id: String = ""        // Id del comercio
    var nombre: String = ""    // Nombre del comercio
    var logo: String? = null   // Logo del comercio
    var banner: String? = null // Banner del comercio
    var millas: Int = 0        // Cantidad de millas
    var rubro: String? = null  // Nombre del Rubro
    var pais:String? = "" //Pais del Comercio
    var colorTarjeta:String? = null // Color de la tarjeta en Wallet
    var fechaSeguimiento: Date = Date()
    var fechaFinSeguimiento: Date? = null
    var permiteMillas: Boolean? = false // Comercio Permitira Millas
    var permitePlanes: Boolean? = false // Comercio permitira Planes
    var estatus: Int = ESTATUS_ACTIVO

    @Exclude
    override fun getCodeValue(): String  {return ""}
    @Exclude
    override fun getTitleValue(): String  {return nombre}
    @Exclude
    override fun getDescValue(): String  {return ""}

    companion object {
        const val ESTATUS_ACTIVO = 1
        const val ESTATUS_INACTIVO = 2
    }
}