package com.syntepro.sueldazo.ui.shop.model

import androidx.annotation.Keep

@Keep
data class CountriesResponse(
        val name: String
) {
    override fun toString(): String {
        return name
    }
}