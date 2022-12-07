package com.appbenefy.sueldazo.ui.login.model

data class ObtenerTokenRequest(
    var country: String,
    var language: Int,
    var idClient: Int
)
