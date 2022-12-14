package com.appbenefy.sueldazo.ui.login.model

data class ValidarClienteResponse(
    var idClient: Int,
    var names: String,
    var lastNames: String,
    var dateBirth: String,
    var nameClient: String,
    var gender: String,
    var maritalStatus: String,
    var email: String,
    var isValidClient: Boolean,
    var age: String,
    var segment: String,
    var isClientBenefy: Boolean,
    var cellPhone: String,
    var sendToken: Boolean,
    var token: String
)
