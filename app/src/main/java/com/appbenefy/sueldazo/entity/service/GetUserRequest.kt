package com.appbenefy.sueldazo.entity.service

data class GetUserRequest(
    var country: String? = null,
    var language: Int = 0,
    var idDocument: Long? = null
)