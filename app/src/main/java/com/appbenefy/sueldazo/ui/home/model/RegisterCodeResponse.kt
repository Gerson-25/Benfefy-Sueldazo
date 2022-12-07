package com.appbenefy.sueldazo.ui.home.model

import androidx.annotation.Keep
import com.appbenefy.sueldazo.entity.service.PresentDetail

@Keep
class RegisterCodeResponse {
    var presentDetail: PresentDetail? = null
    var idProductType: Int = 0
}