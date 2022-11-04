package com.syntepro.sueldazo.ui.profile.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.profile.model.ProfileRepository
import com.syntepro.sueldazo.ui.profile.model.TransactionDetailRequest
import com.syntepro.sueldazo.ui.profile.model.TransactionDetailResponse
import javax.inject.Inject

class DoTransactionDetail
@Inject constructor(private val profileRepository: ProfileRepository) : UseCase<BaseResponse<TransactionDetailResponse>, DoTransactionDetail.Params>() {

    override suspend fun run(params: Params) = profileRepository.getTransactionDetail(params.request)

    data class Params(val request: TransactionDetailRequest)
}