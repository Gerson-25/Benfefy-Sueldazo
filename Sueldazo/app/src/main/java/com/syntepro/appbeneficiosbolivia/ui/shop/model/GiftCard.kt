package com.syntepro.appbeneficiosbolivia.ui.shop.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.entity.service.Agency
import java.io.Serializable

@Keep
class GiftCard: Serializable {
    var idGiftcard: String? = null
    var idCommerce: String? = null
    var giftcardName: String? = null
    var minimumAmount: Double = 0.0
    var bonus: Double? = null
    var conditions: String? = null
    var urlCommerce: String? = null
    var commerceName: String? = null
    var agencies: List<Agency>? = null
    var color: String? = ""
}