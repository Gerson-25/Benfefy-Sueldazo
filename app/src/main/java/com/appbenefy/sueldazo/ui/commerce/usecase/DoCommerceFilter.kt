package com.appbenefy.sueldazo.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.commerce.model.CommerceFilterRequest
import com.appbenefy.sueldazo.ui.commerce.model.CommerceFilterResponse
import com.appbenefy.sueldazo.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerceFilter
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<CommerceFilterResponse>>, DoCommerceFilter.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceFiltered(params.request)

    data class Params(val request: CommerceFilterRequest)
}