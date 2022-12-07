package com.appbenefy.sueldazo.ui.login.model

data class ValidarClienteResponse(
    var idClient: Int,
    var nameClient: String,
    var isValidClient: Boolean,
    var age: String,
    var segment: String,
    var isClientBenefy: Boolean,
    var cellPhone: String,
    var sendToken: Boolean,
    var token: String
)
