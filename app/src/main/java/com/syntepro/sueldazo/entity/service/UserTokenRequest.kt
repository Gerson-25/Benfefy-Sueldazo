package com.syntepro.sueldazo.entity.service

class UserTokenRequest {
    var country: String? = null
    var language: Int = 1 // 1 Spanish - 2 English
    var idUser: String? = null
    var idUserFirebase: String? = null
    var token: String? = null
    var device: String? = null
    var store: Int = 3
}