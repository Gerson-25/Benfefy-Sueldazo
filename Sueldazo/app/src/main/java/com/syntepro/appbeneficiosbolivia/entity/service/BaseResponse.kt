package com.syntepro.appbeneficiosbolivia.entity.service

class BaseResponse<T> {
    var code: Int = 0
    var isSuccess: Boolean = false
    var description: String? = null
    var data: MutableList<T>? = null
}