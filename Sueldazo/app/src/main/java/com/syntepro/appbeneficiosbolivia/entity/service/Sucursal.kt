package com.syntepro.appbeneficiosbolivia.entity.service

import com.syntepro.appbeneficiosbolivia.entity.app.Horarios

class Sucursal {
    var idSucursal: String? = null
    var nombre: String? = null
    var longitud: Double = 0.0
    var latitud: Double = 0.0
    var direccion: String? = null
    var telefono: String? = null
    var horarios: ArrayList<Horarios>? = null
}