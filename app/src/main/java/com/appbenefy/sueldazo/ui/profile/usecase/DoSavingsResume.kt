package com.appbenefy.sueldazo.ui.profile.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.profile.model.ProfileRepository
import com.appbenefy.sueldazo.ui.profile.model.SavingDetailsResponse
import com.appbenefy.sueldazo.ui.profile.model.SavingResumeResponse
import com.appbenefy.sueldazo.ui.profile.model.TransactionRequest
import javax.inject.Inject

class DoSavingsResume
@Inject constructor(private val profileRepository: ProfileRepository) : UseCase<BaseResponse<SavingResumeResponse>, DoSavingsResume.Params>() {

    override suspend fun run(params: Params) = profileRepository.getSavingsResume(params.request)

    data class Params(val request: TransactionRequest)
}