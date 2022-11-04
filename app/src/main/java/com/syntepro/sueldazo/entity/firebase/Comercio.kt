package com.syntepro.sueldazo.entity.firebase

import com.google.firebase.firestore.DocumentId

class Comercio {

    @DocumentId
    var documentID: String? = null
//    @DocumentId
//    var Id: String? = null
    var nombre: String? = null
    var imagen: String? = null
    var banner: String? = null
    var whatsapp: String? = null
    var isActivo: Boolean = false
    var facebook: String? = null
    var instagram: String? = null
    var idRubro: String? = null
    var pais: String? = null
    var descripcion: String? = null
    var colorTarjeta:String? = null
    var telefono: String? = null // Telefono de casa matriz
    var email: String? = null    // Email de contacto principal en casa matriz
    var direccion: String? = null // Direccion de casa matriz
    var website: String? = null
    var horarioServicioInicio: String? = null  // HH:mm Horario de atencio en casa matriz
    var horarioServicioFin: String? = null     // HH:mm
    var latitud: Double? = null  // Coordenas de la casa matriz
    var longitud: Double? = null
    var permiteMillas: Boolean? = false // Comercio Permitira Millas
    var permitePlanes: Boolean? = false // Comercio permitira Planes

//    constructor(documentID: String, nombre: String, imagen: String, banner: String) {
//        this.documentID = documentID
//        this.nombre = nombre
//        this.imagen = imagen
//        this.banner = banner
//    }

}
