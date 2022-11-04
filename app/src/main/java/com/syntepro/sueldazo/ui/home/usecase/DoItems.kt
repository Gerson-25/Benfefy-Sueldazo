package com.syntepro.sueldazo.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.shop.model.ArticleRequest
import com.syntepro.sueldazo.ui.shop.model.ArticleResponse
import com.syntepro.sueldazo.ui.home.model.HomeRepository
import javax.inject.Inject

class DoItems
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<ArticleResponse>>, DoItems.Params>() {

    override suspend fun run(params: Params) = homeRepository.getItems(params.request)

    data class Params(val request: ArticleRequest)
}