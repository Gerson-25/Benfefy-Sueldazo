package com.syntepro.appbeneficiosbolivia.entity.app

import com.google.gson.annotations.SerializedName

data class QRCode(@SerializedName("countExchange") val countExchange: Int, @SerializedName("code") val code: String)