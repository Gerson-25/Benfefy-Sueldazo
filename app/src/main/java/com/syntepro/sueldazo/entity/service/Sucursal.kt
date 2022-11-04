package com.syntepro.sueldazo.entity.service

import com.syntepro.sueldazo.entity.app.Horarios

class Sucursal {
    var idSucursal: String? = null
    var nombre: String? = null
    var longitud: Double = 0.0
    var latitud: Double = 0.0
    var direccion: String? = null
    var telefono: String? = null
    var horarios: ArrayList<Horarios>? = null
}