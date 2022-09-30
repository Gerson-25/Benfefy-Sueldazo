package com.syntepro.appbeneficiosbolivia.ui.home.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.entity.service.PresentDetail

@Keep
class RegisterCodeResponse {
    var presentDetail: PresentDetail? = null
    var idProductType: Int = 0
}