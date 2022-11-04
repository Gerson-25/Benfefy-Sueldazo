package com.syntepro.sueldazo.ui.home.usecase

import com.merckers.core.interactor.UseCase
import com.syntepro.sueldazo.core.entities.BaseResponse
import com.syntepro.sueldazo.ui.home.model.PurchasedProductsResponse
import com.syntepro.sueldazo.ui.home.model.PurchasedProductsRequest
import com.syntepro.sueldazo.ui.home.model.HomeRepository
import javax.inject.Inject

class DoPurchaseProducts
@Inject constructor(private val homeRepository: HomeRepository) : UseCase<BaseResponse<List<PurchasedProductsResponse>>, DoPurchaseProducts.Params>() {

    override suspend fun run(params: Params) = homeRepository.getPurchaseProducts(params.request)

    data class Params(val request: PurchasedProductsRequest)
}