package com.syntepro.appbeneficiosbolivia.ui.lealtad.model

import androidx.annotation.Keep

@Keep
data class CorporateCoupon(
        val idCampana: String,
        val titulo: String,
        val subtitulo: String,
        val precioReal: Double,
        val precioDescuento: Double,
        val imagenCampana: String,
        val imagenComercio: String,
        val tipo: Int,
        val favorite: Boolean
)