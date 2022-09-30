package com.syntepro.appbeneficiosbolivia.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceFilterRequest
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceFilterResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerceFilter
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<CommerceFilterResponse>>, DoCommerceFilter.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceFiltered(params.request)

    data class Params(val request: CommerceFilterRequest)
}