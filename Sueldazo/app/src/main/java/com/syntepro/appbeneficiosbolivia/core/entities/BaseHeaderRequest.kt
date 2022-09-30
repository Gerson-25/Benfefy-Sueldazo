package com.syntepro.appbeneficiosbolivia.core.entities

import com.google.gson.annotations.SerializedName
import java.util.*

data class BaseHeaderRequest (
    @SerializedName("IdProceso")
    var processId: String = "00",
    @SerializedName("IdDevice")
    var deviceId: String = "",
    @SerializedName("IdUsuario")
    var userId: String = "",
    @SerializedName("TimeStamp")
    var timeStamp: String = Date().toString(),
    @SerializedName("IdCia")
    var companyId: Int = 0,
    @SerializedName("token")
    var token: String = ""
)