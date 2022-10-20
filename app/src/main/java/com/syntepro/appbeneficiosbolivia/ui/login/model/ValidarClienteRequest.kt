package com.syntepro.appbeneficiosbolivia.ui.login.model

data class ValidarClienteRequest (
    var country: String,
    var language: Int,
    var documento: Long
    )