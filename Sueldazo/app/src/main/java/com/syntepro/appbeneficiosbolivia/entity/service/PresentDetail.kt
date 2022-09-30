package com.syntepro.appbeneficiosbolivia.entity.service

import androidx.annotation.Keep
import java.io.Serializable
import java.util.*

@Keep
class PresentDetail: Serializable {
    var idPresentOrder: String? = ""
    var commerceName: String? = ""
    var urlImage: String? = ""
    var title: String? = ""
    var subtitle: String? = ""
    var amount: Double? = 0.0
    var idProductType: Int? = 0
    var userGivesName: String? = ""
    var userReceivesName: String? = ""
    var dedicatory: String? = ""
    var presentEndDate: Date = Date()
}