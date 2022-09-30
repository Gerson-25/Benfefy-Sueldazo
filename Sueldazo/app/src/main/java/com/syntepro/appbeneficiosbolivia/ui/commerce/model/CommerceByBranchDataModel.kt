package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Commerce

@Keep
class CommerceByBranchDataModel {
    var type: Int = 0

    // Branch
    var branchId: String = ""
    var name : String = ""
    var urlImage: String? = null
    var commerceCount: Int = 0

    // Commerce
    lateinit var commerce: Commerce

    companion object {
        const val HEADER = 1
        const val DATA = 2
    }
}