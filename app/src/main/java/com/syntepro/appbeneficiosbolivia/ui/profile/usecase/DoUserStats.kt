package com.syntepro.appbeneficiosbolivia.ui.profile.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.profile.model.ProfileRepository
import com.syntepro.appbeneficiosbolivia.ui.profile.model.UserStatsRequest
import com.syntepro.appbeneficiosbolivia.ui.profile.model.UserStatsResponse
import javax.inject.Inject

class DoUserStats
@Inject constructor(private val profileRepository: ProfileRepository) : UseCase<BaseResponse<UserStatsResponse>, DoUserStats.Params>() {

    override suspend fun run(params: Params) = profileRepository.getUserStats(params.request)

    data class Params(val request: UserStatsRequest)
}