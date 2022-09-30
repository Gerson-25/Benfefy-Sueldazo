package com.syntepro.appbeneficiosbolivia.ui.home.usecase

import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.entity.service.Category
import com.syntepro.appbeneficiosbolivia.ui.home.model.CategoryRequest
import com.syntepro.appbeneficiosbolivia.ui.home.model.HomeRepository

import javax.inject.Inject

class DoCategories
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<Category>>, DoCategories.Params>() {

    override suspend fun run(params: Params) = homeRepository.getCategories(params.request)

    data class Params(val request: CategoryRequest)
}
