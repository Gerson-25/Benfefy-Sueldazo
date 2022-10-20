package com.syntepro.appbeneficiosbolivia.ui.login.model

data class ObtenerTokenRequest(
    var country: String,
    var language: Int,
    var documento: String
)
