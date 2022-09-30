package com.syntepro.appbeneficiosbolivia.entity.app

import com.google.gson.annotations.SerializedName

data class UserQRInformation(@SerializedName("username") val username: String, @SerializedName("userID") val userID: String)