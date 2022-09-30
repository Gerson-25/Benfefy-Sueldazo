package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import androidx.annotation.Keep
import com.syntepro.appbeneficiosbolivia.ui.lealtad.model.Commerce

@Keep
data class CommerceByBranchResponse(
        var viewType: Int = 1,
        val branchId: String,
        val name: String,
        val description: String,
        val commerceCount: Int,
        val urlImage: String,
        val commerceList: List<Commerce>
)