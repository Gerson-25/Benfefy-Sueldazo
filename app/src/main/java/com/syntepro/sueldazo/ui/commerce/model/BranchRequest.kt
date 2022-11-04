package com.syntepro.sueldazo.ui.commerce.model

import androidx.annotation.Keep

@Keep
data class BranchRequest(
        var country: String,
        var language: Int
)