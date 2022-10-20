package com.syntepro.appbeneficiosbolivia.ui.login.model

data class ObtenerTokenResponse(
    var transactionValid: Boolean,
    var token: String?,
    var cellPhone: String
)