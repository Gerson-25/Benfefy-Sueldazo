package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class BranchResponse(
        var idRubro: String,
        var nombre: String,
        var imagen: String
)