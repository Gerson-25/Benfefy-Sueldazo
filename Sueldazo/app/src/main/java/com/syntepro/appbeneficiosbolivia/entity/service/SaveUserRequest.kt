package com.syntepro.appbeneficiosbolivia.entity.service

import androidx.annotation.Keep

@Keep
class SaveUserRequest {
    var country: String? = null
    var language: Int = 1 // 1 Spanish - 2 English
    var idUserFirebase: String? = null
    var names: String? = null
    var lastNames: String? = null
    var countryCode: String? = null
    var phone: String? = null
    var birthDate: String? = null
    var gender: String? = null
    var maritalStatus: String? = null
    var state: String? = null
    var city: String? = null
    var photoUrl: String? = null
    var email: String? = null
    var tycDate: String? = null
    var flagTyc: Boolean = false
    var actualCountry: String? = null
    var store: Int = 3
}