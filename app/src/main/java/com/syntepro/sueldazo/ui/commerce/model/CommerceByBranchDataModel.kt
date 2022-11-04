package com.syntepro.sueldazo.ui.commerce.model

import androidx.annotation.Keep

@Keep
class CommerceByBranchDataModel {
    var type: Int = 0

    // Branch
    var branchId: String = ""
    var name : String = ""
    var urlImage: String? = null
    var commerceCount: Int = 0


    companion object {
        const val HEADER = 1
        const val DATA = 2
    }
}