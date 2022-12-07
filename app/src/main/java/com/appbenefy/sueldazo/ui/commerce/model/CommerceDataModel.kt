package com.appbenefy.sueldazo.ui.commerce.model

import androidx.annotation.Keep
import com.appbenefy.sueldazo.entity.firebase.Comercio

@Keep
class CommerceDataModel {
    var type: Int = 0

    //RUBRO
    var Id: String = ""
    var nombre : String = ""
    var imagen: String? = null
    var totalComercios: Int = 0
    var icono: String? = null

    //ITEMS
    var comercio: Comercio = Comercio()

    //VerMas
    var rubroId: String = ""
    var rubroName: String = ""
    var isVerMas = false

    companion object {
        const val HEADER = 1
        const val NORMAL = 2
        const val FOOTER = 3
    }
}