package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep
import com.syntepro.sueldazo.entity.service.PresentDetail

@Keep
class RegisterCodeResponse {
    var presentDetail: PresentDetail? = null
    var idProductType: Int = 0
}