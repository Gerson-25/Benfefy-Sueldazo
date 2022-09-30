package com.syntepro.appbeneficiosbolivia.core.entities

import com.google.gson.annotations.SerializedName

data class BaseServiceRequest<T>(
        @SerializedName("Encabezado")
    val header: BaseHeaderRequest,
        @SerializedName("Parametros")
    val parameters: T
)