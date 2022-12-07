package com.appbenefy.sueldazo.ui.login.model

data class ValidateTokenResponse(
    var transactionValid: Boolean,
    var tokenStatus: String
)