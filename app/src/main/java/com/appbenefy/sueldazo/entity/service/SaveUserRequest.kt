package com.appbenefy.sueldazo.entity.service

import androidx.annotation.Keep

@Keep
class SaveUserRequest {
    var country: String = "BO"
    var language: Int = 1 // 1 Spanish - 2 English
    var idDocument: Long? = null
    var names: String? = null
    var lastNames: String? = null
    var phone: String? = null
    var birthDate: String? = null
    var gender: String? = null
    var maritalStatus: String? = null
    var photoUrl: String? = null
    var email: String? = null
    var actualCountry: String = "BO"
}