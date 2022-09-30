package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import com.syntepro.appbeneficiosbolivia.entity.firebase.Comercio

data class ParentModel (
        val Id: String = "",
        val nombre : String = "",
        val imagen: String? = null,
        val totalComercios: Int = 0,
        val icono: String? = null,
        val children : List<Comercio>
)