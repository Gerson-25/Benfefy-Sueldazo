package com.appbenefy.sueldazo.ui.profile.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.profile.model.ProfileRepository
import com.appbenefy.sueldazo.ui.profile.model.UserStatsRequest
import com.appbenefy.sueldazo.ui.profile.model.UserStatsResponse
import javax.inject.Inject

class DoUserStats
@Inject constructor(private val profileRepository: ProfileRepository) : UseCase<BaseResponse<UserStatsResponse>, DoUserStats.Params>() {

    override suspend fun run(params: Params) = profileRepository.getUserStats(params.request)

    data class Params(val request: UserStatsRequest)
}