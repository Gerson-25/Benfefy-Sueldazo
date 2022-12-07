package com.appbenefy.sueldazo.ui.commerce.model

import com.appbenefy.sueldazo.entity.firebase.Comercio

data class ParentModel (
        val Id: String = "",
        val nombre : String = "",
        val imagen: String? = null,
        val totalComercios: Int = 0,
        val icono: String? = null,
        val children : List<Comercio>
)