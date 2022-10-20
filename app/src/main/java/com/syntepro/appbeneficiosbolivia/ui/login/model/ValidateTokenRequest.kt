package com.syntepro.appbeneficiosbolivia.ui.login.model

data class ValidateTokenRequest(
    var country: String,
    var language: Int,
    var otpCode: String,
    var token: String
)
