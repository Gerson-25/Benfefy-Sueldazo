package com.appbenefy.sueldazo.ui.login.model

data class ValidarClienteRequest (
    var country: String,
    var language: Int,
    var documento: Long
    )