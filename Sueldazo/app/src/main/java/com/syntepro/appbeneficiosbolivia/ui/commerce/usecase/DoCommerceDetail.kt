package com.syntepro.appbeneficiosbolivia.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerceDetail
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<CommerceDetailResponse>, DoCommerceDetail.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceDetail(params.request)

    data class Params(val request: CommerceDetailRequest)
}