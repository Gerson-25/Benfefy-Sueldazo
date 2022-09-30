package com.syntepro.appbeneficiosbolivia.entity.service

import com.syntepro.appbeneficiosbolivia.utils.Constants

open class BaseRequest {
    var country: String? = null
    var language: Int = 1
    var recordsNumber = Constants.LIST_PAGE_SIZE
    var pageNumber: Long = 1
    var sortType: Long = 0 // ASC or DESC
}