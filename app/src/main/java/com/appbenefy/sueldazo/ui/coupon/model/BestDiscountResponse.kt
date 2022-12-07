package com.appbenefy.sueldazo.ui.coupon.model

import androidx.annotation.Keep

@Keep
data class BestDiscountResponse(
        val idCampana: String,
        val titulo: String,
        val subtitulo: String,
        val precioDescuento: Double,
        val precioReal: Double,
        val imagenCampana: String,
        val imagenComercio: String
)