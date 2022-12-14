package com.appbenefy.sueldazo.entity.service

class UserUpdateRequest {
    var country: String? = null
    var language: Int = 0
    var idUser: String? = null
    var photoUrl: String? = null
    var names: String? = null
    var lastNames: String? = null
    var countryUser: String? = null
    var birthDate: String? = null
    var gender: String? = null
    var email: String? = null
    var martialStatus: String? = null
}

class UserAnonymousUpdateRequest {
    var country: String? = null
    var language: Int = 0
    var idUser: String? = null
    var photoUrl: String? = null
    var names: String? = null
    var lastNames: String? = null
    var email: String? = null
    var actualCountry: String? = null
}