package com.appbenefy.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class SudamericanaData(
        val extension: String,
        val subsidiary_code: Int
) {
    override fun toString(): String {
        return extension
    }
}