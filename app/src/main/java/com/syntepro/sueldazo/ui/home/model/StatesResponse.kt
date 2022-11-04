package com.syntepro.sueldazo.ui.home.model

import androidx.annotation.Keep
import com.syntepro.sueldazo.entity.service.City

@Keep
data class StatesResponse(
        val idState: String,
        val name: String,
        val cities: List<City>
) {
    override fun toString(): String {
        return name ?: "-"
    }
}