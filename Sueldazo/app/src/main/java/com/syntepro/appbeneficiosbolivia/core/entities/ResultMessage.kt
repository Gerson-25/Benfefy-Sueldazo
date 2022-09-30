package com.syntepro.appbeneficiosbolivia.core.entities

import com.google.gson.annotations.SerializedName

data class ResultMessage(
    @SerializedName("estado")
    val status: Boolean,
    @SerializedName("mensaje")
    val message: String
)