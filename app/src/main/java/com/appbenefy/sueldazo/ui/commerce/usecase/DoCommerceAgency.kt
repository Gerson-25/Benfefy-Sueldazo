package com.appbenefy.sueldazo.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.agency.model.AgencyResponse
import com.appbenefy.sueldazo.ui.commerce.model.CommerceAgencyRequest
import com.appbenefy.sueldazo.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerceAgency
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<AgencyResponse>>, DoCommerceAgency.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceAgency(params.request)

    data class Params(val request: CommerceAgencyRequest)
}