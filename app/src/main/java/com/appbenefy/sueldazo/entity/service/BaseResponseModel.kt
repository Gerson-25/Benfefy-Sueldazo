package com.appbenefy.sueldazo.entity.service

class BaseResponseModel<T> {
    var code: Int = 0
    var isSuccess: Boolean = false
    var description: String? = null
    var data: T? = null
}