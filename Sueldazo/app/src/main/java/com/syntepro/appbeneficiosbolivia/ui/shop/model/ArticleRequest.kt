package com.syntepro.appbeneficiosbolivia.ui.shop.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.entity.service.BaseRequest

@Keep
class ArticleRequest: BaseRequest() {
    var idCity: String? = null
    var idCategory: String? = null
    var idUser: String = ""
}