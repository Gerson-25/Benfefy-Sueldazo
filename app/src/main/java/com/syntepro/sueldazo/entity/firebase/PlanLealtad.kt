package com.syntepro.sueldazo.entity.firebase

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import syntepro.util.picker.ListablePicker
import java.util.*

class PlanLealtad: ListablePicker {
    @DocumentId
    var id: String = ""
    var categoria: Int = 0
    var nombre: String = ""
    var descripcion: String? = null
    var fechaCreacion = Date()
    var terminos: String? = null         // Terminos y condiciones
    var requiereCodigo: Boolean = false  // Determina si para la afiliacion de usuario requiere codigo de validacion
    var activo: Boolean = true
    var sellos: Int = 0   // Cantidad de sellos por tarjeta (para cateogria de sellos) - debe ser par y menor a 12
    var miembros: Int = 0  // Cantidad de miembros afiliado al plan
    var fechaFin =  Date()   // Fecha Finalizacion del programa de Lealtad
    var imagenSello: String? = null // Imagen con la que se marca cada sello ganado
    var imagenFondo: String? = null // Imagen de fondo de las tarjetas de sellos
    var fechaFinalizacion: Date? = null // Fecha de finalizacion de este programa

    @Exclude
    override fun getCodeValue(): String  {return ""}
    @Exclude
    override fun getTitleValue(): String  {return nombre}
    @Exclude
    override fun getDescValue(): String  {return descripcion?:""}


    companion object {
        const val PLAN_MILLAS = 1
        const val PLAN_AFILIACION = 2
        const val PLAN_COBRANDING = 3
        const val PLAN_SELLOS = 4
    }
}