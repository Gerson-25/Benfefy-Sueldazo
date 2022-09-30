package com.syntepro.appbeneficiosbolivia.ui.commerce.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceAgencyRequest
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceAgencyResponse
import com.syntepro.appbeneficiosbolivia.ui.commerce.model.CommerceRepository
import javax.inject.Inject

class DoCommerceAgency
@Inject constructor(private val commerceRepository: CommerceRepository) : UseCase<BaseResponse<List<AgencyResponse>>, DoCommerceAgency.Params>() {

    override suspend fun run(params: Params) = commerceRepository.getCommerceAgency(params.request)

    data class Params(val request: CommerceAgencyRequest)
}