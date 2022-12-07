package com.appbenefy.sueldazo.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class CommerceByBranchResponse(
        var viewType: Int = 1,
        val branchId: String,
        val name: String,
        val description: String,
        val commerceCount: Int,
        val urlImage: String,
)