package com.appbenefy.sueldazo.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.appbenefy.sueldazo.core.entities.BaseResponse
import com.appbenefy.sueldazo.ui.home.model.FavoriteRequest
import com.appbenefy.sueldazo.ui.home.model.FavoriteResponse
import com.appbenefy.sueldazo.ui.home.model.HomeRepository
import javax.inject.Inject

class DoFavorites
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<FavoriteResponse>>, DoFavorites.Params>() {

    override suspend fun run(params: Params) = homeRepository.getFavorites(params.request)

    data class Params(val request: FavoriteRequest)
}