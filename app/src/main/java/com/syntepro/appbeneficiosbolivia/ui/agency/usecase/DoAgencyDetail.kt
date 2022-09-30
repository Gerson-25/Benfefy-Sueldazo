package com.syntepro.appbeneficiosbolivia.ui.agency.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyDetailResponse
import com.syntepro.appbeneficiosbolivia.ui.agency.model.AgencyRepository
import javax.inject.Inject

class DoAgencyDetail
@Inject constructor(private val agencyRepository: AgencyRepository) : UseCase<BaseResponse<AgencyDetailResponse>, DoAgencyDetail.Params>() {

    override suspend fun run(params: Params) = agencyRepository.getAgencyDetail(params.request)

    data class Params(val request: AgencyDetailRequest)
}