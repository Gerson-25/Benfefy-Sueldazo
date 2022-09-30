package com.syntepro.appbeneficiosbolivia.ui.shop.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.entity.service.BaseRequest

@Keep
class GiftCardRequest: BaseRequest() {
    var idCity: String? = null
    var idCategory: String? = null
}