package com.syntepro.appbeneficiosbolivia.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceByBranchRequest
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceByBranchResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerce
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<CommerceByBranchResponse>>, DoCommerce.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceByBranch(params.request)

    data class Params(val request: CommerceByBranchRequest)
}