package com.syntepro.sueldazo.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.agency.model.AgencyResponse
import com.syntepro.sueldazo.ui.commerce.model.CommerceAgencyRequest
import com.syntepro.sueldazo.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerceAgency
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<AgencyResponse>>, DoCommerceAgency.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceAgency(params.request)

    data class Params(val request: CommerceAgencyRequest)
}