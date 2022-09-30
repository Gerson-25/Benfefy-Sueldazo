package com.syntepro.appbeneficiosbolivia.ui.home.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.entity.service.BaseRequest
import com.syntepro.appbeneficiosbolivia.utils.Constants

@Keep
data class PurchasedProductsRequest(
        var country: String,
        var language: Int,
        var recordsNumber: Int,
        var pageNumber: Long,
        var sortType: Long, // ASC or DESC
        var idUser: String
)