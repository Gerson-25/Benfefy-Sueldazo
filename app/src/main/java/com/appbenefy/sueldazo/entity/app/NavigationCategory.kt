package com.appbenefy.sueldazo.entity.app

import java.util.*

class NavigationCategory {
    var idCategoria: String? = null
    var fechaRegistro: Date? = null

    constructor() {}

    constructor(idCategoria: String?, fechaRegistro: Date?) {
        this.idCategoria = idCategoria
        this.fechaRegistro = fechaRegistro
    }
}