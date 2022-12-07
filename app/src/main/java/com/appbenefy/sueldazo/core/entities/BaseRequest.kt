package com.appbenefy.sueldazo.core.entities

import com.google.gson.annotations.SerializedName

data class BaseRequest<T> (
    @SerializedName("Servicio")
    var service: BaseServiceRequest<T>
)
