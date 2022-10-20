package com.syntepro.appbeneficiosbolivia.ui.login.model

data class ValidateTokenResponse(
    var transactionValid: Boolean,
    var tokenStatus: String
)