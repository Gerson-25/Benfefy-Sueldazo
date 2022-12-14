package com.appbenefy.sueldazo.ui.profile.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.profile.model.ProfileRepository
import com.appbenefy.sueldazo.ui.profile.model.SavingDetailsResponse
import com.appbenefy.sueldazo.ui.profile.model.TransactionRequest
import javax.inject.Inject

class DoUserTransactions
@Inject constructor(private val profileRepository: ProfileRepository) : UseCase<BaseResponse<SavingDetailsResponse>, DoUserTransactions.Params>() {

    override suspend fun run(params: Params) = profileRepository.getUserTransactions(params.request)

    data class Params(val request: TransactionRequest)
}