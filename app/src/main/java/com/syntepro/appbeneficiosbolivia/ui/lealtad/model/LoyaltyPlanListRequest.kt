package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class LoyaltyPlanListRequest(
        val country: String,     // País Actual del Usuario
        val language: Int,       // 0, -- 1 Español – 2 Inglés
        val recordsNumber: Int,  // 0, -- Cantidad de registros a obtener
        val pageNumber: Int,     // 0, -- Número de página
        val idUser: String //,      // -- IdUsuario BD
        //val planType: Int        //  0 -- 1- Corporativo, 2- Millas, 3- Sellos
)