package com.appbenefy.sueldazo.entity.service

import androidx.annotation.Keep

@Keep
data class City(
        val idCity: String,
        val isState: String,
        val name: String,
) {
    override fun toString(): String {
        return name
    }
}