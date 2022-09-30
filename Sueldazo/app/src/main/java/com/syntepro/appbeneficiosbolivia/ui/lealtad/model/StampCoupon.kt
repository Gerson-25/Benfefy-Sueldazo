package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class StampCoupon(
        val idCampana: String,
        val titulo: String,
        val subtitulo: String,
        val imagenCampana: String,
        val imagenComercio: String,
        val disponible: Boolean,
        val sellosRequeridos: Int,
        val tipo: Int,
        val favorite: Boolean
)