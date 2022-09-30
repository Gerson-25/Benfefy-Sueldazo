package com.syntepro.appbeneficiosbolivia.ui.profile.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.profile.model.ProfileRepository
import com.syntepro.appbeneficiosbolivia.ui.profile.model.TransactionDetailRequest
import com.syntepro.appbeneficiosbolivia.ui.profile.model.TransactionDetailResponse
import javax.inject.Inject

class DoTransactionDetail
@Inject constructor(private val profileRepository: ProfileRepository) : UseCase<BaseResponse<TransactionDetailResponse>, DoTransactionDetail.Params>() {

    override suspend fun run(params: Params) = profileRepository.getTransactionDetail(params.request)

    data class Params(val request: TransactionDetailRequest)
}