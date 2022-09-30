package com.syntepro.appbeneficiosbolivia.entity.service

import androidx.annotation.Keep

@Keep
class State {
    var idState: String? = null
    var name: String? = null
    var cities: List<City>? = null

    override fun toString(): String {
        return name ?: "-"
    }
}