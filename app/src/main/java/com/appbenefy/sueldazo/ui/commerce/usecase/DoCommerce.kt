package com.appbenefy.sueldazo.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.commerce.model.CommerceByBranchRequest
import com.appbenefy.sueldazo.ui.commerce.model.CommerceByBranchResponse
import com.appbenefy.sueldazo.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerce
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<CommerceByBranchResponse>>, DoCommerce.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceByBranch(params.request)

    data class Params(val request: CommerceByBranchRequest)
}