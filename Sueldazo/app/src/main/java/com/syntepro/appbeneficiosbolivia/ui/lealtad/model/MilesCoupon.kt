package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class MilesCoupon(
        val idCampana: String,
        val titulo: String,
        val subtitulo: String,
        val miles: Int,
        val imagenCampana: String,
        val disponible: Boolean,
        val tipo: Int
)