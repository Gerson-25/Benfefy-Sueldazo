package com.syntepro.appbeneficiosbolivia.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.appbeneficiosbolivia.core.entities.BaseResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.FavoriteRequest
import com.syntepro.appbeneficiosbolivia.ui.home.model.FavoriteResponse
import com.syntepro.appbeneficiosbolivia.ui.home.model.HomeRepository
import javax.inject.Inject

class DoFavorites
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<FavoriteResponse>>, DoFavorites.Params>() {

    override suspend fun run(params: Params) = homeRepository.getFavorites(params.request)

    data class Params(val request: FavoriteRequest)
}