package com.appbenefy.sueldazo.ui.home.usecase

import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.entity.service.Category
import com.appbenefy.sueldazo.ui.home.model.CategoryRequest
import com.appbenefy.sueldazo.ui.home.model.HomeRepository

import javax.inject.Inject

class DoCategories
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<Category>>, DoCategories.Params>() {

    override suspend fun run(params: Params) = homeRepository.getCategories(params.request)

    data class Params(val request: CategoryRequest)
}
