package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep
import com.syntepro.sueldazo.entity.service.BaseRequest

@Keep
class GiftCardRequest: BaseRequest() {
    var idCity: String? = null
    var idCategory: String? = null
}