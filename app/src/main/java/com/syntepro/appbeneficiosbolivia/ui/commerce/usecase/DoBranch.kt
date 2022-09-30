package com.syntepro.appbeneficiosbolivia.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.BranchRequest
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.BranchResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoBranch
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<BranchResponse>>, DoBranch.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getBranches(params.request)

    data class Params(val request: BranchRequest)
}