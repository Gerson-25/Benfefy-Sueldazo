package com.syntepro.sueldazo.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.commerce.model.CommerceDetailRequest
import com.syntepro.sueldazo.ui.commerce.model.CommerceDetailResponse
import com.syntepro.sueldazo.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerceDetail
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<CommerceDetailResponse>, DoCommerceDetail.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceDetail(params.request)

    data class Params(val request: CommerceDetailRequest)
}