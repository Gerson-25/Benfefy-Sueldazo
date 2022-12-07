package com.appbenefy.sueldazo.ui.home.model

import androidx.annotation.Keep
import com.appbenefy.sueldazo.entity.service.City

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