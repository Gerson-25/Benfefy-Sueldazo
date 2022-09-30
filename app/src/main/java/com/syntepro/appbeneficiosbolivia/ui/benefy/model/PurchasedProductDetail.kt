package com.syntepro.appbeneficiosbolivia.ui.benefy.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.entity.service.Agency
import com.syntepro.appbeneficiosbolivia.entity.service.PresentDetail

@Keep
class PurchasedProductDetail {
    var idProductIndex: Int? = 0
    var productsAvailable: Int = 0
    var title: String? = null
    var subtitle: String? = null
    var percentPrice: Double = 0.0
    var regularPrice: Double = 0.0
    var idProductType: Int = 0
    var idProduct: String? = null
//    var generationDate: Date = Date()
//    var redeemedDate: Date = Date()
//    var redeemedStatus: Boolean = false
    var idComercio: String? = null
    var urlCommerce: String? = null
    var commerceName: String? = null
    var urlArt: String? = null
    var qrCode: String? = null
    var termsConditions: String? = null
    var commerceWhatsapp: String? = null
    var commerceFacebook: String? = null
    var commerceInstagram: String? = null
    var agencies: List<Agency>? = null
    var presentDetail: PresentDetail? = null
}