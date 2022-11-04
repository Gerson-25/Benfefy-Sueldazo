package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep
import com.syntepro.sueldazo.utils.Constants

@Keep
class RegisterCodeRequest {
    var country: String? = null
    var language: Int = 0
    var promCode: String? = null
    var idUser: String? = null
    var store: Int = Constants.PLAY_STORE
    var device: String = "Android"
    var movilBrand: String? = ""
}