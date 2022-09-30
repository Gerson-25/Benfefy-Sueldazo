package com.syntepro.appbeneficiosbolivia.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class CommerceByBranchRequest(
        val country: String,
        val language: Int,
        val recordsNumber: Int,
        val pageNumber: Int,
        val branchesId: List<String>?
)