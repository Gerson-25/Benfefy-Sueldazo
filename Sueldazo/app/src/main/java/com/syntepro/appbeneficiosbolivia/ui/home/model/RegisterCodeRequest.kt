package com.syntepro.appbeneficiosbolivia.ui.home.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.utils.Constants

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