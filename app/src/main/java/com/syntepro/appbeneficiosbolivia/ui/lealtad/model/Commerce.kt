package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class Commerce(
        var viewType: Int = 2,
        val idComercio: String,
        val nombre: String,
        val urlImage : String,
        val categoryName: String
)