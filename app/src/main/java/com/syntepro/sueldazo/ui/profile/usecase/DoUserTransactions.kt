package com.syntepro.sueldazo.ui.profile.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.profile.model.ProfileRepository
import com.syntepro.sueldazo.ui.profile.model.TransactionRequest
import com.syntepro.sueldazo.ui.profile.model.TransactionResponse
import javax.inject.Inject

class DoUserTransactions
@Inject constructor(private val profileRepository: ProfileRepository) : UseCase<BaseResponse<List<TransactionResponse>>, DoUserTransactions.Params>() {

    override suspend fun run(params: Params) = profileRepository.getUserTransactions(params.request)

    data class Params(val request: TransactionRequest)
}