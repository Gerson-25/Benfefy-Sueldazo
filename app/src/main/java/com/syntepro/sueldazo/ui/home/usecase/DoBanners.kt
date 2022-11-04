package com.syntepro.sueldazo.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.home.model.BannerResponse
import com.syntepro.sueldazo.ui.home.model.BannerRequest
import com.syntepro.sueldazo.ui.home.model.HomeRepository
import javax.inject.Inject

class DoBanners
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<BannerResponse>>, DoBanners.Params>() {

    override suspend fun run(params: Params) = homeRepository.getBanners(params.request)

    data class Params(val request: BannerRequest)
}