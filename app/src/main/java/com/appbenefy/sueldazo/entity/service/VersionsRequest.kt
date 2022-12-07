package com.appbenefy.sueldazo.entity.service

import com.appbenefy.sueldazo.utils.Constants

class VersionsRequest {
    var country: String? = null
    var language: Int = 0
    var parameterCode: String = Constants.PLAY_STORE_VERSION
}