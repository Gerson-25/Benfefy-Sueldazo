package com.appbenefy.sueldazo.ui.home.model

import androidx.annotation.Keep

@Keep
data class StatesRequest(
        val country: String,
        val language: Int
)