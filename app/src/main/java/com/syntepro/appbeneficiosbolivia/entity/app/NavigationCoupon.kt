package com.syntepro.appbeneficiosbolivia.entity.app

import java.util.*

class NavigationCoupon {
    var idCupon: String? = null
    var fechaRegistro: Date? = null

    constructor() {}

    constructor(idCupon: String?, fechaRegistro: Date?) {
        this.idCupon = idCupon
        this.fechaRegistro = fechaRegistro
    }
}