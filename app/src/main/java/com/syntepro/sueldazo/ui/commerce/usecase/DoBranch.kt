package com.syntepro.sueldazo.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.commerce.model.BranchRequest
import com.syntepro.sueldazo.ui.commerce.model.BranchResponse
import com.syntepro.sueldazo.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoBranch
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<BranchResponse>>, DoBranch.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getBranches(params.request)

    data class Params(val request: BranchRequest)
}